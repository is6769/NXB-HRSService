package org.example.hrsservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.requests.UsageWithMetadataDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TarifficationUsageConsumerService {

    @RabbitListener(queues = "${const.rabbitmq.tariffication.CALL_USAGE_QUEUE_NAME}")
    public void consumeCallUsage(UsageWithMetadataDTO usageWithMetadataDTO){
        log.info(usageWithMetadataDTO.toString());
    }
}
