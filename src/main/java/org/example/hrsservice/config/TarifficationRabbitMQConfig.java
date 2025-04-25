package org.example.hrsservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class TarifficationRabbitMQConfig {

    @Value("${const.rabbitmq.tariffication.CALL_USAGE_QUEUE_NAME}")
    private String CALL_USAGE_QUEUE_NAME;

    @Value("${const.rabbitmq.tariffication.TARIFFICATION_EXCHANGE_NAME}")
    private String TARIFFICATION_EXCHANGE_NAME;

    @Value("${const.rabbitmq.tariffication.CALL_USAGE_ROUTING_KEY}")
    private String CALL_USAGE_ROUTING_KEY;

    @Bean
    public Queue callUsageQueue(){
        return new Queue(CALL_USAGE_QUEUE_NAME);
    }

    @Bean
    public TopicExchange tarifficationExchange(){
        return new TopicExchange(TARIFFICATION_EXCHANGE_NAME,false,false);
    }

    @Bean
    public Binding callUsageBinding(){
        return BindingBuilder
                .bind(callUsageQueue())
                .to(tarifficationExchange())
                .with(CALL_USAGE_ROUTING_KEY);
    }
}
