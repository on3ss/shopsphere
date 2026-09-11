package dev.shopsphere.order.product;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
public class RestProductClient implements ProductClient {

    private final RestClient restClient;

    public RestProductClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("http://product-service")
                .build();
    }

    @Override
    public Product getProduct(UUID productId) {

        try {
            return restClient
                    .get()
                    .uri("/api/v1/products/{id}", productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ProductClientException(
                                response.getStatusCode().value(),
                                "Product service returned an error"
                        );
                    })
                    .body(Product.class);

        } catch (RestClientResponseException ex) {
            throw new ProductClientException(
                    ex.getStatusCode().value(),
                    "Unable to retrieve product"
            );
        }
    }

    public static class ProductClientException extends RuntimeException {

        private final int status;

        public ProductClientException(int status, String message) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}