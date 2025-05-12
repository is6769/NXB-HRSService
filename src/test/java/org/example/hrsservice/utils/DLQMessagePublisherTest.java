package org.example.hrsservice.utils;

import org.example.hrsservice.services.SystemDatetimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для класса {@link DLQMessagePublisher}.
 * Проверяет корректность публикации сообщений в Dead Letter Queue (DLQ).
 */
@ExtendWith(MockitoExtension.class)
class DLQMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private SystemDatetimeService systemDatetimeService;

    @InjectMocks
    private DLQMessagePublisher dlqMessagePublisher;

    private final String exchangePostfix = ".dlx";
    private final String routingKeyPostfix = ".dlq";
    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dlqMessagePublisher, "DEAD_LETTER_EXCHANGE_POSTFIX", exchangePostfix);
        ReflectionTestUtils.setField(dlqMessagePublisher, "DEAD_LETTER_ROUTING_KEY_POSTFIX", routingKeyPostfix);
        when(systemDatetimeService.getSystemDatetime()).thenReturn(testDateTime);
    }

    /**
     * Тестирует метод {@link DLQMessagePublisher#publishToDLQ(Message, Throwable)}.
     * Проверяет, что сообщение обогащается необходимой информацией об ошибке и отправляется в DLQ.
     */
    @Test
    void publishToDLQ_shouldEnhanceMessageAndSend() {
        String originalExchange = "test-exchange";
        String originalRoutingKey = "test-routing-key";
        String errorMessage = "Test error message";
        
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setReceivedExchange(originalExchange);
        messageProperties.setReceivedRoutingKey(originalRoutingKey);
        
        Message originalMessage = new Message("test-message".getBytes(), messageProperties);
        RuntimeException exception = new RuntimeException(errorMessage);

        dlqMessagePublisher.publishToDLQ(originalMessage, exception);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).convertAndSend(
            eq(originalExchange + exchangePostfix), 
            eq(originalRoutingKey + routingKeyPostfix),
            messageCaptor.capture()
        );
        
        Message enhancedMessage = messageCaptor.getValue();
        assertNotNull(enhancedMessage);
        assertEquals(RuntimeException.class.getName(), enhancedMessage.getMessageProperties().getHeader("x-exception-type"));
        assertEquals(errorMessage, enhancedMessage.getMessageProperties().getHeader("x-error-msg"));
        assertEquals(testDateTime, enhancedMessage.getMessageProperties().getHeader("x-timestamp"));
        
        verify(systemDatetimeService).getSystemDatetime();
    }
}
