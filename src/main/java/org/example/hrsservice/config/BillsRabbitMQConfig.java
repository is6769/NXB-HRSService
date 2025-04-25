package org.example.hrsservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class BillsRabbitMQConfig {

    @Value("${const.rabbitmq.bills.BILLS_QUEUE_NAME}")
    private String BILLS_QUEUE_NAME;

    @Value("${const.rabbitmq.bills.BILLS_EXCHANGE_NAME}")
    private String BILLS_EXCHANGE_NAME;

    @Value("${const.rabbitmq.bills.BILLS_ROUTING_KEY}")
    private String BILLS_ROUTING_KEY;

    @Bean
    public Queue billsQueue(){
        return new Queue(BILLS_QUEUE_NAME);
    }

    @Bean
    public DirectExchange billsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME,false,false);
    }

    @Bean
    public Binding billsBinding(){
        return BindingBuilder
                .bind(billsQueue())
                .to(billsExchange())
                .with(BILLS_ROUTING_KEY);
    }
}
