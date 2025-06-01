package com.om.controller;

import com.om.dm.Store;
import com.om.service.StoreService;

import java.util.List;

/**
 * Handles client requests related to store operations,
 * such as saving, deleting, retrieving, and updating stores.
 */
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // Save a new store
    public void save(Store store) {
        storeService.addStore(store.getName(), store.getLocationId());
    }

    // Delete a store by its ID
    public void delete(Store store) {
        storeService.deleteStore(store.getId());
    }

    // Get a store by its ID
    public Store get(Store store) {
        return storeService.getStoreById(store.getId());
    }

    // Get all stores
    public List<Store> getAll() {
        return storeService.getAllStores();
    }

    // Update store details
    public void update(Store store) {
        storeService.updateStore(store);
    }
}
