package org.example.hrsservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ для очередей и обменников, связанных со счетами (bills).
 * Определяет основную очередь для счетов и соответствующую Dead Letter Queue (DLQ).
 */
@Configuration
public class BillsRabbitMQConfig {

    /**
     * Имя основной очереди для счетов.
     */
    @Value("${const.rabbitmq.bills.BILLS_QUEUE_NAME}")
    private String BILLS_QUEUE_NAME;

    /**
     * Имя основного обменника для счетов.
     */
    @Value("${const.rabbitmq.bills.BILLS_EXCHANGE_NAME}")
    private String BILLS_EXCHANGE_NAME;

    /**
     * Ключ маршрутизации для основной очереди счетов.
     */
    @Value("${const.rabbitmq.bills.BILLS_ROUTING_KEY}")
    private String BILLS_ROUTING_KEY;

    /**
     * Постфикс для имени обменника DLQ.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_EXCHANGE_POSTFIX}")
    private String DEAD_LETTER_EXCHANGE_POSTFIX;

    /**
     * Постфикс для ключа маршрутизации DLQ.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_ROUTING_KEY_POSTFIX}")
    private String DEAD_LETTER_ROUTING_KEY_POSTFIX;

    /**
     * Постфикс для имени очереди DLQ.
     */
    @Value("${const.rabbitmq.dead-letter.DEAD_LETTER_QUEUE_POSTFIX}")
    private String DEAD_LETTER_QUEUE_POSTFIX;

    /**
     * Создает бин для Dead Letter Queue (DLQ) счетов.
     * @return Объект {@link Queue} для DLQ.
     */
    @Bean
    public Queue deadLetterBillsQueue(){
        return new Queue(BILLS_QUEUE_NAME+DEAD_LETTER_QUEUE_POSTFIX);
    }

    /**
     * Создает бин для обменника DLQ счетов.
     * @return Объект {@link DirectExchange} для DLQ.
     */
    @Bean
    public DirectExchange deadLetterBillsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME+DEAD_LETTER_EXCHANGE_POSTFIX,false,false);
    }

    /**
     * Создает бин для привязки DLQ счетов к ее обменнику.
     * @return Объект {@link Binding}.
     */
    @Bean
    public Binding deadLetterBillsBinding(){
        return BindingBuilder
                .bind(deadLetterBillsQueue())
                .to(deadLetterBillsExchange())
                .with(BILLS_ROUTING_KEY+DEAD_LETTER_ROUTING_KEY_POSTFIX);
    }

    /**
     * Создает бин для основной очереди счетов.
     * @return Объект {@link Queue}.
     */
    @Bean
    public Queue billsQueue(){
        return new Queue(BILLS_QUEUE_NAME);
    }

    /**
     * Создает бин для основного обменника счетов.
     * @return Объект {@link DirectExchange}.
     */
    @Bean
    public DirectExchange billsExchange(){
        return new DirectExchange(BILLS_EXCHANGE_NAME,false,false);
    }

    /**
     * Создает бин для привязки основной очереди счетов к ее обменнику.
     * @return Объект {@link Binding}.
     */
    @Bean
    public Binding billsBinding(){
        return BindingBuilder
                .bind(billsQueue())
                .to(billsExchange())
                .with(BILLS_ROUTING_KEY);
    }
}
