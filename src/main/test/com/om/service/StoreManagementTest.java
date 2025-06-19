package com.om.service;

import com.om.algorithm.Edge;
import com.om.dm.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Store Management Tests")
public class StoreManagementTest {

    private StoreService storeService;

    @BeforeEach
    void setUp() {
        TestDao testDao = new TestDao();
        storeService = new StoreService(testDao);
    }

    @Test
    @DisplayName("Should add store successfully")
    void testAddStore() {
        storeService.addStore("Test Store", "A");
        List<Store> stores = storeService.getAllStores();
        assertFalse(stores.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when adding store to non-existent location")
    void testAddStoreToNonExistentLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addStore("Test Store", "Z");
        });
    }

    @Test
    @DisplayName("Should get all stores")
    void testGetAllStores() {
        storeService.addStore("Store 1", "A");
        storeService.addStore("Store 2", "B");
        
        List<Store> stores = storeService.getAllStores();
        
        assertNotNull(stores);
        assertEquals(2, stores.size());
    }

    @Test
    @DisplayName("Should get store by ID")
    void testGetStoreById() {
        storeService.addStore("Test Store", "A");
        
        Store store = storeService.getStoreById(1);
        
        assertNotNull(store);
        assertEquals("Test Store", store.getName());
    }

    @Test
    @DisplayName("Should throw exception when getting store with invalid ID")
    void testGetStoreByIdWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.getStoreById(0);
        });
    }

    @Test
    @DisplayName("Should update store successfully")
    void testUpdateStore() {
        storeService.addStore("Original Store", "A");
        Store store = new Store(1, "Updated Store", 0.0, 0.0, "A");
        
        storeService.updateStore(store);
        
        Store updatedStore = storeService.getStoreById(1);
        assertEquals("Updated Store", updatedStore.getName());
    }

    @Test
    @DisplayName("Should delete store successfully")
    void testDeleteStore() {
        storeService.addStore("Test Store", "A");
        
        storeService.deleteStore(1);
        
        List<Store> stores = storeService.getAllStores();
        assertTrue(stores.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when deleting store with invalid ID")
    void testDeleteStoreWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.deleteStore(0);
        });
    }

    // TestDao implementation for this test class
    private static class TestDao implements com.om.dao.IDao {
        private java.util.Map<String, double[]> nodes = new java.util.HashMap<>();
        private java.util.Map<String, java.util.List<Edge>> edges = new java.util.HashMap<>();
        private java.util.List<Store> stores = new java.util.ArrayList<>();
        private java.util.Map<Integer, java.util.List<com.om.dm.Product>> storeProducts = new java.util.HashMap<>();
        private int nextStoreId = 1;

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
            store.setId(nextStoreId++);
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
        public void addProductToStore(int storeId, com.om.dm.Product product) {
            storeProducts.computeIfAbsent(storeId, k -> new java.util.ArrayList<>()).add(product);
        }

        @Override
        public void removeProductFromStore(int storeId, String productName) {
            java.util.List<com.om.dm.Product> products = storeProducts.get(storeId);
            if (products != null) {
                products.removeIf(p -> p.getName().equals(productName));
            }
        }

        @Override
        public java.util.List<com.om.dm.Product> getProductsByStoreId(int storeId) {
            return storeProducts.getOrDefault(storeId, new java.util.ArrayList<>());
        }

        @Override
        public void updateProductInStore(int storeId, com.om.dm.Product product) {
            java.util.List<com.om.dm.Product> products = storeProducts.get(storeId);
            if (products != null) {
                products.removeIf(p -> p.getId() == product.getId());
                products.add(product);
            }
        }

        @Override
        public Store findNearestStoreWithProduct(String userLocationId, String productName, java.util.Map<String, java.util.List<Edge>> graph) {
            for (Store store : stores) {
                java.util.List<com.om.dm.Product> products = getProductsByStoreId(store.getId());
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
                java.util.List<com.om.dm.Product> products = getProductsByStoreId(store.getId());
                for (com.om.dm.Product product : products) {
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
            nextStoreId = 1;
        }
    }
} 