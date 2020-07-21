package cn.xlucky.framework.kafka.interceptors;

import java.util.Iterator;
import java.util.Map;

import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.log.LogTracing;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

/**
 * 消费者uuid
 * @author xlucky
 * @date 2020/7/21
 * @version 1.0.0
 */
public class KafkaConsumerInterceptor implements ConsumerInterceptor {

    public KafkaConsumerInterceptor() {
    }

    @Override
    public ConsumerRecords onConsume(ConsumerRecords records) {
        Object consumerRecordObj = records.iterator().next();
        if (consumerRecordObj != null && consumerRecordObj instanceof ConsumerRecord) {
            ConsumerRecord consumerRecord = (ConsumerRecord)consumerRecordObj;
            Headers headers = consumerRecord.headers();
            Iterable<Header> traceHeaderIterable = headers.headers(CommonConstant.LOG_TRACE_ID);
            Iterator<Header> traceHeaderIt = traceHeaderIterable.iterator();
            String traceId = null;
            if (traceHeaderIt.hasNext()) {
                Header traceHeader = traceHeaderIt.next();
                if (traceHeader != null) {
                    traceId = new String(traceHeader.value());
                }
            }

            LogTracing.start(traceId);
        }

        return records;
    }
    @Override
    public void close() {
        LogTracing.end();
    }
    @Override
    public void onCommit(Map offsets) {
    }
    @Override
    public void configure(Map<String, ?> configs) {
    }
}