package dev.shopsphere.order.order;

public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}