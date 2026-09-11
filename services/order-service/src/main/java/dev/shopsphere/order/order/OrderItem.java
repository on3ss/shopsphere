package dev.shopsphere.order.order;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private int quantity;

    protected OrderItem() {
    }

    public OrderItem(
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            String currency,
            int quantity) {

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.currency = currency;
        this.quantity = quantity;
    }

    void attachTo(Order order) {
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}