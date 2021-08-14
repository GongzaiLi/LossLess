package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.transaction.Transactional;
import java.util.List;

/**
 * InventoryRepository is a repository interface for Inventory objects.
 * This declares 'raw' accessors to Inventory JPA objects. Don't use this class to get/update Inventory items. Use the
 * InventoryService instead as it may have business logic implemented.
 */
@RepositoryRestResource
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Inventory findFirstById(Integer id);

    List<Inventory> findAllByBusinessIdAndQuantityGreaterThanAndProductIdContainsAllIgnoreCase(Integer id, Integer quantity, String productName, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update Inventory set quantityInListing = :newQuantity where id = :inventoryId")
    Integer updateInventoryQuantityInListing(@Param("newQuantity") Integer newQuantity, @Param("inventoryId") Integer inventoryId);

    Integer countInventoryByBusinessIdAndQuantityGreaterThanAndProductIdContainsAllIgnoreCase(Integer id, Integer quantity, String productName);
}
