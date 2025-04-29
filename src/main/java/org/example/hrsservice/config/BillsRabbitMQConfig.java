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

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX}")
    private String DEAD_LETTER_EXCHANGE_POSTFIX;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX}")
    private String DEAD_LETTER_ROUTING_KEY_POSTFIX;

    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_QUEUE_POSTFIX}")
    private String DEAD_LETTER_QUEUE_POSTFIX;

    @Bean
    public Queue deadLetterBillsQueue(){
        return new Queue(BILLS_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    @Bean
    public DirectExchange deadLetterBillsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    @Bean
    public Binding deadLetterBillsBinding(){
        return BindingBuilder
                .bind(deadLetterBillsQueue())
                .to(deadLetterBillsExchange())
                .with(BILLS_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

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
