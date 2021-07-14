package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This is the handler for persisting and retrieving Inventory Data. Business logic for saving, modifying inventory
 * items goes here.
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    /**
     * Returns the Inventory item with the given id
     *
     * @param id        The id of the Inventory item to be found
     * @return          The found Inventory item, if any, otherwise null
     */
    public Inventory findInventoryById(Integer id) {
        return inventoryRepository.findFirstById(id);
    }


    /**
     * Given an Inventory object, 'creates' it by saving and persisting it in the database.
     * @param inventory The inventory item to create
     * @return The created inventory item. The returned item should have a valid database id you can get with .getId()
     */
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    /**
     * Given an Inventory object, 'modifies' it by saving and persisting the object in the database.
     * Note that this method is currently functionally identical to the createInventory method. The only difference is that
     * the saved object is not returned, as you should already have the database id in the object you passed in.
     * @param inventory The inventory item to update
     */
    public void updateInventory(Inventory inventory) {
        inventoryRepository.save(inventory);
    }

    /**
     * Get the entire inventory of items for a given business
     *
     * @param id The id of the business
     * @return A list containing every item in the business' inventory.
     * Returns an empty list if there are no items in the business' inventory, or if the business does not exist
     */
    public List<Inventory> getInventoryFromBusinessId(Integer id) { return  inventoryRepository.findAllByBusinessId(id); }

    /**
     * Updates the quantity column of the inventory table in the database using a custom sql set statement.
     *
     * @param newQuantity The new quantity remaining for the inventory item
     * @param inventoryId The inventory id of the inventory item
     * @return            Returns the updated inventory item entity
     */
    public Integer updateInventoryItemQuantity(Integer newQuantity, Integer inventoryId) { return inventoryRepository.updateInventoryQuantity(newQuantity, inventoryId); }

}
