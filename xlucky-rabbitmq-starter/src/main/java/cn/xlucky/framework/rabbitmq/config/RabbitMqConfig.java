package cn.xlucky.framework.rabbitmq.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;


/**
 * mq配置
 * @author xlucky
 * @date 2020/6/19
 * @version 1.0.0
 */
@Configuration
@ConditionalOnProperty(
    prefix = "rabbitmq",
    name = {"address"}
)
@ConfigurationProperties(
    prefix = "rabbitmq"
)
@Slf4j
@Data
public class RabbitMqConfig {
    public static final String RABBIT_MANUAL_CONTAINER_FACTORY = "ms_manualContainerFactory";
    public static final String RABBIT_NONE_CONTAINER_FACTORY = "ms_noneContainerFactory";
    public static final String RABBIT_CONNECTION_FACTORY = "ms_connectionFactory";
    private String address;
    private String username;
    private String password;
    private String virtualHost;
    private int concurrency;
    private int prefetchCount;

    public RabbitMqConfig() {
    }

    @Bean({RABBIT_CONNECTION_FACTORY})
    @Primary
    public ConnectionFactory connectionFactory() {
        log.info("====== RabbitMQ ======");
        log.info("RabbitMQ address {}", this.address);
        log.info("RabbitMQ username {}", this.username);
        log.info("RabbitMQ password {}", "******");
        log.info("RabbitMQ virtual host {}", this.virtualHost);
        log.info("====== RabbitMQ ======\n");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(this.address);
        connectionFactory.setUsername(this.username);
        connectionFactory.setPassword(this.password);
        connectionFactory.setVirtualHost(this.virtualHost);
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean(name = {RABBIT_MANUAL_CONTAINER_FACTORY})
    public SimpleRabbitListenerContainerFactory manualContainerFactory(@Qualifier(RABBIT_CONNECTION_FACTORY) ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(this.prefetchCount);
        factory.setConcurrentConsumers(this.concurrency);
        factory.setMessageConverter(this.rabbitMessageConverter());
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置当rabbitmq收到nack/reject确认信息时的处理方式，设为true，扔回queue头部，设为false，丢弃。
        factory.setDefaultRequeueRejected(Boolean.FALSE);
        return factory;
    }

    @Bean(name = {RABBIT_NONE_CONTAINER_FACTORY})
    public SimpleRabbitListenerContainerFactory noneContainerFactory(@Qualifier(RABBIT_CONNECTION_FACTORY) ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(this.prefetchCount);
        factory.setConcurrentConsumers(this.concurrency);
        factory.setMessageConverter(this.rabbitMessageConverter());
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        return factory;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Primary
    public RabbitTemplate rabbitTemplate(@Qualifier(RABBIT_CONNECTION_FACTORY) ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    @Primary
    public MessageConverter rabbitMessageConverter() {
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        return messageConverter;
    }
}