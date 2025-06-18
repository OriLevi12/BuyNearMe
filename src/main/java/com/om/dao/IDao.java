package com.om.dao;
import com.om.algorithm.*;
import java.util.Map;

import com.om.dm.Store;
import com.om.dm.Product;
import java.util.List;

public interface IDao {

    // Add a new store
    void addStore(Store store);

    // Retrieve all stores
    List<Store> getAllStores();

    // Retrieve a store by its ID
    Store getStoreById(int id);

    // Update an existing store
    void updateStore(Store store);

    // Delete a store by ID
    void deleteStore(int id);

    // Add a product to a specific store
    void addProductToStore(int storeId, Product product);

    // Remove a product from a specific store
    void removeProductFromStore(int storeId, String productName);

    // Get all products in a specific store
    List<Product> getProductsByStoreId(int storeId);

    // Update a product in a specific store
    void updateProductInStore(int storeId, Product product);

    // Find the nearest store with path information
    Store findNearestStoreWithProduct(String userLocationId, String productName, Map<String, List<Edge>> graph);

    // Graph persistence methods
    void saveNode(String nodeName, double x, double y);

    void removeNode(String nodeName);

    void saveEdge(String from, String to, double weight);

    void removeEdge(String from, String to);

    List<String> getAllNodes();
    
    Map<String, double[]> getAllNodesWithCoordinates();

    Map<String, List<Edge>> getAllEdges();

    // Clear all data
    void clearAll();
}