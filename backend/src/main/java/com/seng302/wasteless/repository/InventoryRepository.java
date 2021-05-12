package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * ProductRepository is a repository interface for Product.
 * Used to declare accessors to JPA objects.
 */
@RepositoryRestResource
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findFirstById(String id);

    static List<Inventory> findAllByBusinessId(Integer id) {          // this needs to be implemented
        return null;
    }
}
