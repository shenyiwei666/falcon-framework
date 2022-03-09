package org.falconframework.logging.gather;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.falconframework.logging.LoggingContext;
import org.falconframework.logging.dto.ElkLogging;
import org.falconframework.logging.dto.LoggingConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * kafka采集日志
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/1/11
 */
public class KafkaGather implements LoggingGather {

    private LoggingConfig config = LoggingContext.getLoggingConfig();
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private KafkaProducer<String, String> producer;
    private boolean isStarted = false;

    @Override
    public void write(ElkLogging elkLogging) {
        if (!this.isStarted) {
            start();
        }
        String message = JSON.toJSONString(elkLogging);
        write(message);
    }

    private void write(String message) {
        this.queue.offer(message);
    }

    private synchronized void start() {
        if (this.isStarted) {
            return;
        }
        initProducer();
        startSender();
        this.isStarted = true;
    }

    private void initProducer() {
        LoggingConfig.KafkaConfig kafkaConfig = this.config.getKafka();
        Map<String, Object> producerConfig = new HashMap();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getServers());
        producerConfig.put(ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks());
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, kafkaConfig.getRetries());
        producerConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaConfig.getCompressionType());
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfig.getBufferMemory());
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfig.getBatchSize());
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfig.getLingerMs());
        producerConfig.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, kafkaConfig.getMaxRequestSize());
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConfig.getRequestTimeoutMs());
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producer = new KafkaProducer(producerConfig);
    }

    private void startSender() {
        new Thread(() -> {
            while (true) {
                send();
            }
        }).start();
    }

    private void send() {
        String message = takeQueueMessage();
        if (message == null) {
            return;
        }
        String topic = this.config.getKafka().getTopic();
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, message);
        this.producer.send(producerRecord, (RecordMetadata metadata, Exception exception) -> {
            if (exception != null) {
                processSendError(message, metadata, exception);
            }
        });
    }

    private String takeQueueMessage() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private void processSendError(String message, RecordMetadata metadata, Exception e) {
        System.out.println("日志发送kafka失败：" + message);
        e.printStackTrace();
    }

}
