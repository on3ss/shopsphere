package dev.shopsphere.order.order;

public enum OrderStatus {
    PENDING,

    RESERVING_INVENTORY,

    CONFIRMED,

    CANCELLED,

    PROCESSING,

    SHIPPED,

    DELIVERED
}
