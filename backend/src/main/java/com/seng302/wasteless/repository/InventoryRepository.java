package com.seng302.wasteless.repository;


import com.seng302.wasteless.model.Inventory;
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

    List<Inventory> findAllByBusinessId(Integer id);

    @Modifying
    @Transactional
    @Query(value = "update Inventory set quantity = :newQuantity where id = :inventoryId")
    Integer updateInventoryQuantity(@Param("newQuantity") Integer newQuantity, @Param("inventoryId") Integer inventoryId);
}
