package cn.xlucky.framework.kafka.config;
import cn.xlucky.framework.kafka.interceptors.KafkaConsumerInterceptor;
import cn.xlucky.framework.kafka.interceptors.KafkaProducerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 装配类
 * @author xlucky
 * @date 2020/7/21
 * @version 1.0.0
 */
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties properties) {
        Map<String, Object> config = properties.buildProducerProperties();
        // 2 构建拦截链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(KafkaProducerInterceptor.class.getName());
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,interceptors);

        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(config);
        String transactionIdPrefix = properties.getProducer()
                .getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        return factory;
    }

    @Bean
    public ConsumerFactory<?, ?> kafkaConsumerFactory(KafkaProperties properties) {
        Map<String, Object> config = properties.buildConsumerProperties();
        // 2 构建拦截链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(KafkaConsumerInterceptor.class.getName());
        config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,interceptors);

        return new DefaultKafkaConsumerFactory<>(config);
    }

}