package org.falconframework.logging.gather;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.falconframework.logging.config.ConfigReader;
import org.falconframework.logging.elk.ElkLogging;
import org.falconframework.logging.config.LoggingConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class KafkaLoggingGather implements LoggingGather {

    private LoggingConfig config;
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private KafkaProducer<String, String> producer;
    private boolean started = false;

    @Override
    public void write(ElkLogging elkLogging) {
        if (!this.started) {
            start();
        }
        String message = JSON.toJSONString(elkLogging);
        write(message);
    }

    private void write(String message) {
        this.queue.offer(message);
    }

    private synchronized void start() {
        if (this.started) {
            return;
        }
        initConfig();
        initProducer();
        startSender();
        this.started = true;
    }

    private void initConfig() {
        this.config = ConfigReader.getLoggingConfig();
    }

    private void initProducer() {
        LoggingConfig.KafkaConfig kafkaConfig = config.getKafka();
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
//            System.out.println("\nsend kafka ---> " + JSON.parseObject(message, ElkLogging.class).getBody());
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
        log.error("日志发送kafka失败，message={}，metadata={}" + message, JSON.toJSONString(metadata), e);
    }

}
