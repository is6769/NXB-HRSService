package org.example.hrsservice.config;

import com.rabbitmq.client.Channel;
import org.example.hrsservice.exceptions.handlers.RabbitExceptionsHandler;
import org.example.hrsservice.utils.DLQMessagePublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Тесты для класса {@link RabbitExceptionsHandler}.
 * Проверяет корректность обработки ошибок при прослушивании сообщений RabbitMQ.
 */
@ExtendWith(MockitoExtension.class)
class RabbitExceptionsHandlerTest {

    @Mock
    private DLQMessagePublisher dlqMessagePublisher;
    
    @InjectMocks
    private RabbitExceptionsHandler rabbitExceptionsHandler;

    /**
     * Тестирует метод RabbitExceptionsHandler#handleError}.
     * Проверяет, что при возникновении исключения сообщение публикуется в DLQ (Dead Letter Queue).
     * @throws Exception если возникает ошибка во время теста.
     */
    @Test
    void handleError_publishesToDLQ() throws Exception {
        Message message = mock(Message.class);
        ListenerExecutionFailedException exception = new ListenerExecutionFailedException("Test exception", new RuntimeException("Cause"));

        rabbitExceptionsHandler.handleError(message,null, null, exception);

        verify(dlqMessagePublisher).publishToDLQ(message, exception.getCause());
    }
}
