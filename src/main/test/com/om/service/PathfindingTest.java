package com.om.service;

import com.om.algorithm.AStarAlgoShortestPathImpl;
import com.om.algorithm.DijkstraAlgoShortestPathImpl;
import com.om.algorithm.Edge;
import com.om.dm.Product;
import com.om.dm.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pathfinding Tests")
public class PathfindingTest {

    private StoreService storeService;
    private StoreService storeServiceWithAStar;

    @BeforeEach
    void setUp() {
        TestDao testDao = new TestDao();
        storeService = new StoreService(testDao);
        storeServiceWithAStar = new StoreService(testDao, new AStarAlgoShortestPathImpl());
    }

    @Test
    @DisplayName("Should find closest store with product")
    void testFindClosestStoreWithProduct() {
        storeService.addStore("Test Store", "A");
        Product product = new Product(1, "Test Product", 10.0);
        storeService.addProductToStore(1, product);
        
        Store closestStore = storeService.findClosestStoreWithProduct("A", "Test Product");
        
        assertNotNull(closestStore);
        assertEquals("Test Store", closestStore.getName());
    }

    @Test
    @DisplayName("Should throw exception when finding closest store with null product name")
    void testFindClosestStoreWithNullProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.findClosestStoreWithProduct("A", null);
        });
    }

    @Test
    @DisplayName("Should throw exception when finding closest store with empty product name")
    void testFindClosestStoreWithEmptyProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.findClosestStoreWithProduct("A", "");
        });
    }

    @Test
    @DisplayName("Should throw exception when finding closest store from non-existent location")
    void testFindClosestStoreFromNonExistentLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.findClosestStoreWithProduct("Z", "Test Product");
        });
    }

    @Test
    @DisplayName("Should find cheapest store with product")
    void testFindCheapestStoreWithProduct() {
        storeService.addStore("Cheap Store", "A");
        Product product = new Product(1, "Test Product", 5.0);
        storeService.addProductToStore(1, product);
        
        Store cheapestStore = storeService.findCheapestStoreWithProduct("Test Product");
        
        assertNotNull(cheapestStore);
        assertEquals("Cheap Store", cheapestStore.getName());
    }

    @Test
    @DisplayName("Should throw exception when finding cheapest store with null product name")
    void testFindCheapestStoreWithNullProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.findCheapestStoreWithProduct(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when finding cheapest store with empty product name")
    void testFindCheapestStoreWithEmptyProductName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.findCheapestStoreWithProduct("");
        });
    }

    @Test
    @DisplayName("Should switch to A* algorithm")
    void testUseAStarAlgorithm() {
        assertDoesNotThrow(() -> storeService.useAStarAlgorithm());
    }

    @Test
    @DisplayName("Should switch to Dijkstra algorithm")
    void testUseDijkstraAlgorithm() {
        assertDoesNotThrow(() -> storeService.useDijkstraAlgorithm());
    }

    @Test
    @DisplayName("Should compare Dijkstra vs A* algorithms")
    void testAlgorithmComparison() {
        storeService.addStore("Test Store", "A");
        Product product = new Product(1, "Test Product", 10.0);
        storeService.addProductToStore(1, product);
        
        Store dijkstraResult = storeService.findClosestStoreWithProduct("A", "Test Product");
        Store aStarResult = storeServiceWithAStar.findClosestStoreWithProduct("A", "Test Product");
        
        assertNotNull(dijkstraResult);
        assertNotNull(aStarResult);
        assertEquals(dijkstraResult.getName(), aStarResult.getName());
    }

    @Test
    @DisplayName("Should handle complete workflow: add node, store, product, and find store")
    void testCompleteWorkflow() {
        storeService.addNode("E", 3.0, 3.0);
        storeService.addStore("Integration Store", "E");
        Product product = new Product(1, "Integration Product", 15.0);
        storeService.addProductToStore(1, product);
        
        Store foundStore = storeService.findClosestStoreWithProduct("A", "Integration Product");
        
        assertNotNull(foundStore);
        assertEquals("Integration Store", foundStore.getName());
    }

    // TestDao implementation for this test class
    private static class TestDao implements com.om.dao.IDao {
        private java.util.Map<String, double[]> nodes = new java.util.HashMap<>();
        private java.util.Map<String, java.util.List<Edge>> edges = new java.util.HashMap<>();
        private java.util.List<Store> stores = new java.util.ArrayList<>();
        private java.util.Map<Integer, java.util.List<Product>> storeProducts = new java.util.HashMap<>();
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
            nextStoreId = 1;
        }
    }
} 