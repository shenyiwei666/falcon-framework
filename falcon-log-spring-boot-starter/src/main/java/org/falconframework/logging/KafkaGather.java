/*
 * 深圳市灵智数科有限公司版权所有.
 */
package org.falconframework.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.falconframework.logging.config.LoggingConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * kafka采集日志
 *
 * @author 申益炜
 * @version 1.0.0
 * @date 2022/1/11
 */
class KafkaGather {

    private static KafkaGather instance = new KafkaGather();

    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    private LoggingConfig loggingConfig;

    private KafkaProducer<String, String> producer;


    public static KafkaGather getInstance() {
        return instance;
    }

    public void start(LoggingConfig config) {
        init(config);

        new Thread(() -> {
            while (true) {
                send();
            }
        }).start();
    }

    public void write(String loggingBody) {
        queue.offer(loggingBody);
    }

    private void init(LoggingConfig config) {
        loggingConfig = config;
        LoggingConfig.Kafka kafka = loggingConfig.getKafka();
        Map<String, Object> producerConfig = new HashMap();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getServers());
        producerConfig.put(ProducerConfig.ACKS_CONFIG, kafka.getAcks());
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, kafka.getRetries());
        producerConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafka.getCompressionType());
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafka.getBufferMemory());
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, kafka.getBatchSize());
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, kafka.getLingerMs());
        producerConfig.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, kafka.getMaxRequestSize());
        producerConfig.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafka.getRequestTimeoutMs());
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producer = new KafkaProducer(producerConfig);
    }

    private void send() {
        String loggingBody = queueTake();
        send(loggingBody);
    }

    private void send(String message) {
        if (producer == null) {
            write(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(loggingConfig.getKafka().getTopic(), message);
            producer.send(producerRecord, (RecordMetadata metadata, Exception exception) -> {
                System.out.println("发送成功↓↓↓↓↓↓\n" + message);
                if (exception != null) {
                    processSendError(message, metadata, exception);
                }
            });
        } catch (Throwable e) {
            System.out.println("日志发送kafka异常：" + message);
            e.printStackTrace();
        }
    }

    private String queueTake() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private void processSendError(String loggingBody, RecordMetadata metadata, Exception exception) {
        System.out.println("日志发送kafka失败：" + loggingBody);
        exception.printStackTrace();
        // TODO 申益炜 2022/1/13 23:49 记录本地文件，重发
    }

}
