package org.example.hrsservice.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Индикатор состояния здоровья, проверяющий регистрацию сервиса в Eureka.
 * Используется Spring Boot Actuator для предоставления информации о статусе регистрации.
 */
@Component
public class EurekaRegistrationHealthIndicator implements HealthIndicator {

    @Value("${spring.application.name}")
    private String serviceName;

    private final EurekaDiscoveryClient eurekaDiscoveryClient;


    public EurekaRegistrationHealthIndicator(EurekaDiscoveryClient eurekaDiscoveryClient) {
        this.eurekaDiscoveryClient = eurekaDiscoveryClient;
    }

    /**
     * Выполняет проверку состояния регистрации сервиса в Eureka.
     * @return {@link Health} объект, указывающий статус (UP, DOWN) и детали проверки.
     */
    @Override
    public Health health() {
        try {
            List<ServiceInstance> instances =eurekaDiscoveryClient.getInstances(serviceName);
            boolean isRegistered = !instances.isEmpty();
            return (isRegistered)
                    ? Health.up().withDetail("message", "Service is registered in eureka.").build()
                    : Health.down().withDetail("message", "Service is not registered in eureka.").build();
        } catch (Exception e){
            return Health.down(e).build();
        }
    }
}
