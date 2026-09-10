package dev.shopsphere.gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final String HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(
            @NonNull ServerWebExchange exchange,
            @NonNull GatewayFilterChain chain
    ) {
        String correlationId = exchange
                .getRequest()
                .getHeaders()
                .getFirst(HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ServerHttpRequest request = exchange
                .getRequest()
                .mutate()
                .header(HEADER, correlationId)
                .build();

        ServerWebExchange mutatedExchange = exchange
                .mutate()
                .request(request)
                .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .set(HEADER, correlationId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}