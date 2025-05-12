package org.example.hrsservice.exceptions.handlers;

import com.rabbitmq.client.Channel;
import org.example.hrsservice.utils.DLQMessagePublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Обработчик ошибок для слушателей RabbitMQ.
 * При возникновении исключения во время обработки сообщения, данному обработчику передается управление.
 * Он публикует исходное сообщение и причину ошибки в DLQ (Dead Letter Queue).
 */
@Component
public class RabbitExceptionsHandler implements RabbitListenerErrorHandler {

    private final DLQMessagePublisher dlqMessagePublisher;

    public RabbitExceptionsHandler(DLQMessagePublisher dlqMessagePublisher) {
        this.dlqMessagePublisher = dlqMessagePublisher;
    }

    /**
     * Обрабатывает ошибку, возникшую в слушателе RabbitMQ.
     * Если у исключения есть причина (cause), сообщение и причина публикуются в DLQ.
     * В противном случае, исходное исключение пробрасывается дальше.
     *
     * @param amqpMessage Исходное сообщение AMQP.
     * @param channel Канал RabbitMQ.
     * @param message Сообщение Spring Messaging.
     * @param exception Исключение, возникшее при обработке сообщения.
     * @return null, если сообщение успешно отправлено в DLQ, иначе пробрасывает исключение.
     * @throws Exception Если исходное исключение не имеет причины и пробрасывается дальше.
     */
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
