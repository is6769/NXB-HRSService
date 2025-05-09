package org.example.hrsservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TarifficationUsageConsumerService {

    @Value("${const.rabbitmq.bills.BILLS_ROUTING_KEY}")
    private String BILLS_ROUTING_KEY;

    @Value("${const.rabbitmq.bills.BILLS_EXCHANGE_NAME}")
    private String BILLS_EXCHANGE_NAME;

    private final TariffService tariffService;
    private final RabbitTemplate rabbitTemplate;

    public TarifficationUsageConsumerService(TariffService tariffService, RabbitTemplate rabbitTemplate) {
        this.tariffService = tariffService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${const.rabbitmq.tariffication.CALL_USAGE_QUEUE_NAME}",errorHandler = "rabbitExceptionsHandler")
    public void consumeCallUsageAndSendBill(UsageWithMetadataDTO usageWithMetadataDTO){
        log.info(usageWithMetadataDTO.toString());
        TarifficationBillDTO bill = tariffService.chargeCall(usageWithMetadataDTO);
        rabbitTemplate.convertAndSend(BILLS_EXCHANGE_NAME,BILLS_ROUTING_KEY,bill);
    }
}
