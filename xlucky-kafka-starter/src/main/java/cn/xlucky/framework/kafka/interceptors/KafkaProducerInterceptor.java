package cn.xlucky.framework.kafka.interceptors;

import java.util.Map;

import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.log.LogTracing;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

/**
 * 生产者uuid
 * @author xlucky
 * @date 2020/7/21
 * @version 1.0.0
 */
public class KafkaProducerInterceptor implements ProducerInterceptor {

    public KafkaProducerInterceptor() {
    }
    @Override
    public ProducerRecord onSend(ProducerRecord producerRecord) {
        Headers headers = producerRecord.headers();
        if (headers != null) {
            String traceId = LogTracing.getTraceId();
            if (traceId != null) {
                headers.add(CommonConstant.LOG_TRACE_ID, traceId.getBytes());
            }
        }

        return producerRecord;
    }
    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
    }
    @Override
    public void close() {
    }
    @Override
    public void configure(Map<String, ?> map) {
    }
}