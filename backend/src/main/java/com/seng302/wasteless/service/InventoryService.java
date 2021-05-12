package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Inventory;
import com.seng302.wasteless.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.seng302.wasteless.repository.InventoryRepository.*;

/**
 * Inventory service applies product logic over the Inventory JPA repository.
 */
@Service
public class InventoryService {


    /**
     * Todo This is a placeholder until the real implementation is made in task S302T700-91
     * @param inventory The inventory item to create
     */
    public Inventory createInventory(Inventory inventory) {
        return inventory;
    }

    /**
     * Todo This is a placeholder until the real implementation is made in task S302T700-91
     * @param id The inventory item to get from the business
     */
    public List<Inventory> getInventory(Integer id) {
       return InventoryRepository.findAllByBusinessId(id);
    }

}
