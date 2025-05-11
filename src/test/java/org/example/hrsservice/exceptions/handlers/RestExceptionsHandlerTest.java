package org.example.hrsservice.exceptions.handlers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.hrsservice.dtos.ExceptionDTO;
import org.example.hrsservice.exceptions.*;
import org.example.hrsservice.services.SystemDatetimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestExceptionsHandlerTest {

    @Mock
    private SystemDatetimeService systemDatetimeService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RestExceptionsHandler restExceptionsHandler;

    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 5, 15, 12, 0);
    private final String testUrl = "http://example.com/api/test";

    @BeforeEach
    void setUp() {
        when(systemDatetimeService.getSystemDatetime()).thenReturn(testDateTime);
        when(request.getRequestURL()).thenReturn(new StringBuffer(testUrl));
    }

    @Test
    @DisplayName("handleCannotChargeCallException should return correct ExceptionDTO with NOT_FOUND status")
    void handleCannotChargeCallException_returnsCorrectExceptionDTO() {
        String errorMessage = "Cannot charge call due to unexpected behavior";
        CannotChargeCallException exception = new CannotChargeCallException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleCannotChargeCallException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.exceptionType()).isEqualTo("NOT_FOUND");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleInvalidCallMetadataException should return correct ExceptionDTO with BAD_REQUEST status")
    void handleInvalidCallMetadataException_returnsCorrectExceptionDTO() {
        String errorMessage = "Invalid call metadata provided";
        InvalidCallMetadataException exception = new InvalidCallMetadataException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleInvalidCallMetadataException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.exceptionType()).isEqualTo("BAD_REQUEST");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleNoSuchSubscriberTariffException should return correct ExceptionDTO with BAD_REQUEST status")
    void handleNoSuchSubscriberTariffException_returnsCorrectExceptionDTO() {
        String errorMessage = "No tariff found for subscriber";
        NoSuchSubscriberTariffException exception = new NoSuchSubscriberTariffException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleNoSuchSubscriberTariffException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.exceptionType()).isEqualTo("BAD_REQUEST");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleNoSuchTariffException should return correct ExceptionDTO with NOT_FOUND status")
    void handleNoSuchTariffException_returnsCorrectExceptionDTO() {
        String errorMessage = "Tariff with ID 123 not found";
        NoSuchTariffException exception = new NoSuchTariffException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleNoSuchTariffException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.exceptionType()).isEqualTo("NOT_FOUND");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleSubscriberWithInactiveTariffException should return correct ExceptionDTO with BAD_REQUEST status")
    void handleSubscriberWithInactiveTariffException_returnsCorrectExceptionDTO() {
        String errorMessage = "Subscriber has inactive tariff";
        SubscriberWithInactiveTariffException exception = new SubscriberWithInactiveTariffException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleSubscriberWithInactiveTariffException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.exceptionType()).isEqualTo("BAD_REQUEST");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleUnsupportedConditionTypeException should return correct ExceptionDTO with INTERNAL_SERVER_ERROR status")
    void handleUnsupportedConditionTypeException_returnsCorrectExceptionDTO() {
        String errorMessage = "Unsupported condition type";
        UnsupportedConditionTypeException exception = new UnsupportedConditionTypeException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleUnsupportedConditionTypeException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.exceptionType()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }

    @Test
    @DisplayName("handleUnsupportedOperatorException should return correct ExceptionDTO with INTERNAL_SERVER_ERROR status")
    void handleUnsupportedOperatorException_returnsCorrectExceptionDTO() {
        String errorMessage = "Unsupported operator";
        UnsupportedOperatorException exception = new UnsupportedOperatorException(errorMessage);

        ExceptionDTO result = restExceptionsHandler.handleUnsupportedOperatorException(request, exception);

        assertThat(result.timestamp()).isEqualTo(testDateTime);
        assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.exceptionType()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(result.message()).isEqualTo(errorMessage);
        assertThat(result.url()).isEqualTo(testUrl);
    }
}
