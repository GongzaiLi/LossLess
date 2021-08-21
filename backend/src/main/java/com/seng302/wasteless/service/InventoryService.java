package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.repository.InventoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * This is the handler for persisting and retrieving Inventory Data. Business logic for saving, modifying inventory
 * items goes here.
 */
@Service
public class InventoryService {

    private static final Logger logger = LogManager.getLogger(InventoryService.class.getName());

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

        Inventory inventoryItem = inventoryRepository.findFirstById(id);
        if (inventoryItem == null) {
            logger.warn("Request Failed, inventory item does not exist in the inventory");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item with given id does not exist");
        }
        return inventoryItem;
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
    public Inventory updateInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    /**
     * Get the entire inventory of items for a given business. Will not return inventory items whose quantities
     * are equal to zero
     *
     * @param id The id of the business
     * @return A list containing every item in the business' inventory.
     * Returns an empty list if there are no items in the business' inventory, or if the business does not exist
     */
    public List<Inventory> searchInventoryFromBusinessId(Integer id, String searchQuery, Pageable pageable) {
        return inventoryRepository.findAllByBusinessIdAndQuantityGreaterThanAndProductIdContainsAllIgnoreCase(id, 0,  searchQuery, pageable); }

    /**
     * Updates the quantity column of the inventory table in the database using a custom sql set statement.
     *
     * @param quantityRemaining The keep quantity In Listing for the inventory item
     * @param inventoryId The inventory id of the inventory item
     * @return            Returns the updated inventory item entity
     */
    public Integer updateInventoryItemQuantity(Integer quantityRemaining, Integer inventoryId) { return inventoryRepository.updateInventoryQuantityInListing(quantityRemaining, inventoryId); }

    /**
     * Get the count of inventory items of a business. Does not count inventory items whose quantities
     * are equal to zero
     *
     * @param id   The id of the business to get the inventory count of
     * @return     Amount of inventory items in database for that business
     */
    public Integer getTotalInventoryCountByBusinessId(Integer id, String searchQuery) {
        return inventoryRepository.countInventoryByBusinessIdAndQuantityGreaterThanAndProductIdContainsAllIgnoreCase(id, 0, searchQuery);
    }

    /**
     * when edit inventory quantity, then update the inventory quantity unlisted.
     * @param inventory the inventory is edited
     * @param editedInventoryQuantity the edit inventory's new quantity number.
     */
    public void updateQuantityUnlisted(Inventory inventory, Integer editedInventoryQuantity) {

        int quantityUnlisted = inventory.getQuantityUnlisted() + editedInventoryQuantity - inventory.getQuantity();
        if (quantityUnlisted < 0) {
            logger.warn("Request Failed, Inventory's quantity unlisted can not lease then 0");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory's quantity unlisted can not lease then 0.");
        }
        inventory.setQuantityUnlisted(quantityUnlisted);
    }


}
