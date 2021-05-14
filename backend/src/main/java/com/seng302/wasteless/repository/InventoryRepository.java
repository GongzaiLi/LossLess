package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * InventoryRepository is a repository interface for Inventory objects.
 * This declares 'raw' accessors to Inventory JPA objects. Don't use this class to get/update Inventory items. Use the
 * InventoryService instead as it may have business logic implemented.
 */
@RepositoryRestResource
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findFirstById(Long id);

    Inventory findFirstByProduct(String id);

    List<Inventory> findAllByBusinessId(Integer id);
}
