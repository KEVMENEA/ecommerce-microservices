package com.ecommerce.order_service.config;


import com.ecommerce.order_service.client.inventory.InventoryReservationClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class InventoryClientConfig {

    @Bean
    @Primary
    public RestClient.Builder inventoryRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public InventoryReservationClient inventoryReservationClient(
            @LoadBalanced RestClient.Builder loadBalancedRestClientBuilder
    ) {

        RestClient restClient = loadBalancedRestClientBuilder
                .baseUrl("http://inventory-service")
                .build();

        RestClientAdapter adapter =
                RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory
                        .builderFor(adapter)
                        .build();

        return factory.createClient(
                InventoryReservationClient.class
        );
    }
}
