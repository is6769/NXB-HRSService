package org.example.hrsservice.exceptions.handlers;

import com.rabbitmq.client.Channel;
import org.example.hrsservice.utils.DLQMessagePublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RabbitExceptionsHandler implements RabbitListenerErrorHandler {

    private final DLQMessagePublisher dlqMessagePublisher;

    public RabbitExceptionsHandler(DLQMessagePublisher dlqMessagePublisher) {
        this.dlqMessagePublisher = dlqMessagePublisher;
    }

    @Override
    public Object handleError(Message amqpMessage, Channel channel, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        Throwable cause = exception.getCause();
        if (Objects.nonNull(cause)){
            dlqMessagePublisher.publishToDLQ(amqpMessage,cause);
            return null;
        }
        throw exception;
    }
}
