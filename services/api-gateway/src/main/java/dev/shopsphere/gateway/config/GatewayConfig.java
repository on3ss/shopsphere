package dev.shopsphere.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${app.routes.product-service-uri:lb://PRODUCT-SERVICE}")
    private String productServiceUri;

    @Value("${app.routes.order-service-uri:lb://ORDER-SERVICE}")
    private String orderServiceUri;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r
                        .path("/api/v1/products", "/api/v1/products/**")
                        .uri(productServiceUri))
                .route("order-service", r -> r
                        .path("/api/v1/orders", "/api/v1/orders/**")
                        .uri(orderServiceUri))
                .build();
    }
}