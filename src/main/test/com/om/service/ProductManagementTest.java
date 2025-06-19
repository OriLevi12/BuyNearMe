package com.om.service;

import com.om.algorithm.Edge;
import com.om.dm.Product;
import com.om.dm.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for product management operations within stores.
 * Tests the product-related functionality of the StoreService class.
 * 
 * Test Coverage:
 * - Product addition to stores
 * - Product removal from stores
 * - Product updates and modifications
 * - Product retrieval by store ID
 * - Error handling for invalid product operations
 * - Validation of product data integrity
 */
@DisplayName("Product Management Tests")
public class ProductManagementTest {

    private StoreService storeService;

    /**
     * Set up a fresh StoreService instance with test data before each test.
     * Creates a test graph and initializes the service for product operations.
     */
    @BeforeEach
    void setUp() {
        TestDao testDao = new TestDao();
        storeService = new StoreService(testDao);
    }

    /**
     * Test successful product addition to a store.
     * Verifies that products are properly added with correct data and auto-generated ID.
     */
    @Test
    @DisplayName("Should add product to store successfully")
    void testAddProductToStore() {
        Product product = new Product(1, "Test Product", 10.0);
        storeService.addProductToStore(1, product);
        List<Product> products = storeService.getProductsByStoreId(1);
        assertFalse(products.isEmpty());
        assertEquals("Test Product", products.get(0).getName());
    }

    /**
     * Test validation: cannot add null product to store.
     * Ensures proper error handling when product is null.
     */
    @Test
    @DisplayName("Should throw exception when adding null product")
    void testAddNullProductToStore() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addProductToStore(1, null);
        });
    }

    /**
     * Test successful product removal from a store by name.
     * Verifies that products are properly removed from the store.
     */
    @Test
    @DisplayName("Should remove product from store successfully")
    void testRemoveProductFromStore() {
        Product product = new Product(1, "Test Product", 10.0);
        storeService.addProductToStore(1, product);
        
        storeService.removeProductFromStore(1, "Test Product");
        
        List<Product> products = storeService.getProductsByStoreId(1);
        assertTrue(products.isEmpty());
    }

    /**
     * Test validation: cannot remove product with null name.
     * Ensures proper error handling when product name is null.
     */
    @Test
    @DisplayName("Should throw exception when removing product with null name")
    void testRemoveProductWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.removeProductFromStore(1, null);
        });
    }

    /**
     * Test validation: cannot remove product with empty name.
     * Ensures proper error handling when product name is empty.
     */
    @Test
    @DisplayName("Should throw exception when removing product with empty name")
    void testRemoveProductWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.removeProductFromStore(1, "");
        });
    }

    /**
     * Test successful product retrieval by store ID.
     * Verifies that all products in a store can be retrieved correctly.
     */
    @Test
    @DisplayName("Should get products by store ID")
    void testGetProductsByStoreId() {
        Product product1 = new Product(1, "Product 1", 10.0);
        Product product2 = new Product(2, "Product 2", 20.0);
        storeService.addProductToStore(1, product1);
        storeService.addProductToStore(1, product2);
        
        List<Product> products = storeService.getProductsByStoreId(1);
        
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    /**
     * Test validation: cannot get products with invalid store ID.
     * Ensures proper error handling for invalid store IDs.
     */
    @Test
    @DisplayName("Should throw exception when getting products with invalid store ID")
    void testGetProductsByStoreIdWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.getProductsByStoreId(0);
        });
    }

    /**
     * Test successful product update in a store.
     * Verifies that product information can be modified and persisted.
     */
    @Test
    @DisplayName("Should update product in store successfully")
    void testUpdateProductInStore() {
        Product originalProduct = new Product(1, "Original Product", 10.0);
        storeService.addProductToStore(1, originalProduct);
        Product updatedProduct = new Product(1, "Updated Product", 15.0);
        
        storeService.updateProductInStore(1, updatedProduct);
        
        List<Product> products = storeService.getProductsByStoreId(1);
        assertEquals("Updated Product", products.get(0).getName());
        assertEquals(15.0, products.get(0).getPrice());
    }

    /**
     * Test validation: cannot update null product.
     * Ensures proper error handling when updating with null product.
     */
    @Test
    @DisplayName("Should throw exception when updating null product")
    void testUpdateNullProductInStore() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.updateProductInStore(1, null);
        });
    }

    /**
     * Test validation: cannot update product with invalid ID.
     * Ensures proper error handling when product ID is invalid.
     */
    @Test
    @DisplayName("Should throw exception when updating product with invalid ID")
    void testUpdateProductWithInvalidId() {
        Product product = new Product(0, "Test Product", 10.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.updateProductInStore(1, product);
        });
    }

    /**
     * Test DAO implementation for this test class.
     * Provides a mock data access layer with predefined test data and product management.
     * 
     * Features:
     * - Product storage and retrieval
     * - Store management for product operations
     * - Graph data for store location validation
     */
    // TestDao implementation for this test class
    private static class TestDao implements com.om.dao.IDao {
        private java.util.Map<String, double[]> nodes = new java.util.HashMap<>();
        private java.util.Map<String, java.util.List<Edge>> edges = new java.util.HashMap<>();
        private java.util.List<Store> stores = new java.util.ArrayList<>();
        private java.util.Map<Integer, java.util.List<Product>> storeProducts = new java.util.HashMap<>();

        public TestDao() {
            nodes.put("A", new double[]{0.0, 0.0});
            nodes.put("B", new double[]{1.0, 1.0});
            nodes.put("C", new double[]{2.0, 0.0});
            nodes.put("D", new double[]{1.0, -1.0});
            
            edges.put("A", java.util.Arrays.asList(new Edge("A", "B", 1.5), new Edge("A", "D", 2.0)));
            edges.put("B", java.util.Arrays.asList(new Edge("B", "A", 1.5), new Edge("B", "C", 1.0)));
            edges.put("C", java.util.Arrays.asList(new Edge("C", "B", 1.0), new Edge("C", "D", 1.5)));
            edges.put("D", java.util.Arrays.asList(new Edge("D", "A", 2.0), new Edge("D", "C", 1.5)));
        }

        @Override
        public void addStore(Store store) {
            stores.add(store);
        }

        @Override
        public java.util.List<Store> getAllStores() {
            return new java.util.ArrayList<>(stores);
        }

        @Override
        public Store getStoreById(int id) {
            return stores.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }

        @Override
        public void updateStore(Store store) {
            stores.removeIf(s -> s.getId() == store.getId());
            stores.add(store);
        }

        @Override
        public void deleteStore(int id) {
            stores.removeIf(s -> s.getId() == id);
            storeProducts.remove(id);
        }

        @Override
        public void addProductToStore(int storeId, Product product) {
            storeProducts.computeIfAbsent(storeId, k -> new java.util.ArrayList<>()).add(product);
        }

        @Override
        public void removeProductFromStore(int storeId, String productName) {
            java.util.List<Product> products = storeProducts.get(storeId);
            if (products != null) {
                products.removeIf(p -> p.getName().equals(productName));
            }
        }

        @Override
        public java.util.List<Product> getProductsByStoreId(int storeId) {
            return storeProducts.getOrDefault(storeId, new java.util.ArrayList<>());
        }

        @Override
        public void updateProductInStore(int storeId, Product product) {
            java.util.List<Product> products = storeProducts.get(storeId);
            if (products != null) {
                products.removeIf(p -> p.getId() == product.getId());
                products.add(product);
            }
        }

        @Override
        public Store findNearestStoreWithProduct(String userLocationId, String productName, java.util.Map<String, java.util.List<Edge>> graph) {
            for (Store store : stores) {
                java.util.List<Product> products = getProductsByStoreId(store.getId());
                if (products.stream().anyMatch(p -> p.getName().equals(productName))) {
                    return store;
                }
            }
            return null;
        }

        @Override
        public Store findCheapestStoreWithProduct(String productName) {
            Store cheapestStore = null;
            double cheapestPrice = Double.MAX_VALUE;
            
            for (Store store : stores) {
                java.util.List<Product> products = getProductsByStoreId(store.getId());
                for (Product product : products) {
                    if (product.getName().equals(productName) && product.getPrice() < cheapestPrice) {
                        cheapestPrice = product.getPrice();
                        cheapestStore = store;
                    }
                }
            }
            return cheapestStore;
        }

        @Override
        public void saveNode(String nodeName, double x, double y) {
            nodes.put(nodeName, new double[]{x, y});
        }

        @Override
        public void removeNode(String nodeName) {
            nodes.remove(nodeName);
            edges.remove(nodeName);
        }

        @Override
        public void saveEdge(String from, String to, double weight) {
            edges.computeIfAbsent(from, k -> new java.util.ArrayList<>()).add(new Edge(from, to, weight));
        }

        @Override
        public void removeEdge(String from, String to) {
            java.util.List<Edge> fromEdges = edges.get(from);
            if (fromEdges != null) {
                fromEdges.removeIf(e -> e.getToNode().equals(to));
            }
        }

        @Override
        public java.util.List<String> getAllNodes() {
            return new java.util.ArrayList<>(nodes.keySet());
        }

        @Override
        public java.util.Map<String, double[]> getAllNodesWithCoordinates() {
            return new java.util.HashMap<>(nodes);
        }

        @Override
        public java.util.Map<String, java.util.List<Edge>> getAllEdges() {
            return new java.util.HashMap<>(edges);
        }

        @Override
        public void clearAll() {
            nodes.clear();
            edges.clear();
            stores.clear();
            storeProducts.clear();
        }
    }
} 