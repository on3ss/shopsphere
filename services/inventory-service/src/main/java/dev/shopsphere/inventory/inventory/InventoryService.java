package dev.shopsphere.inventory.inventory;

import dev.shopsphere.inventory.exception.InsufficientStockException;
import dev.shopsphere.inventory.exception.InventoryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Inventory getByProductId(UUID productId) {
        return repository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));
    }

    @Transactional
    public Inventory create(UUID productId, int quantity) {
        if (repository.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException(
                    "Inventory already exists for product " + productId
            );
        }

        return repository.save(
                new Inventory(productId, quantity)
        );
    }

    @Transactional
    public void reserve(UUID productId, int quantity) {

        Inventory inventory = repository
                .findWithLockByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(productId));

        if (inventory.getAvailable() < quantity) {
            throw new InsufficientStockException(
                    productId,
                    quantity,
                    inventory.getAvailable()
            );
        }

        inventory.reserve(quantity);
    }
}