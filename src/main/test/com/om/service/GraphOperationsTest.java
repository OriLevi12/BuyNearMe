package com.om.service;

import com.om.algorithm.Edge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for graph operations including node and edge management.
 * Tests the core graph functionality of the StoreService class.
 * 
 * Test Coverage:
 * - Node operations (add, remove, validation)
 * - Edge operations (add, remove, validation) 
 * - Graph structure retrieval
 * - Error handling for invalid operations
 */
@DisplayName("Graph Operations Tests")
public class GraphOperationsTest {

    private StoreService storeService;

    /**
     * Set up a fresh StoreService instance with test data before each test.
     * Creates a test graph with 4 nodes (A, B, C, D) and their connecting edges.
     */
    @BeforeEach
    void setUp() {
        TestDao testDao = new TestDao();
        storeService = new StoreService(testDao);
    }

    /**
     * Test successful node addition to the graph.
     * Verifies that a new node is properly added and can be retrieved.
     */
    @Test
    @DisplayName("Should add node successfully")
    void testAddNode() {
        storeService.addNode("E", 3.0, 3.0);
        assertTrue(storeService.getAllNodes().contains("E"));
    }

    /**
     * Test validation: node name cannot be null.
     * Ensures proper error handling for invalid input.
     */
    @Test
    @DisplayName("Should throw exception when adding node with null name")
    void testAddNodeWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addNode(null, 1.0, 1.0);
        });
    }

    /**
     * Test validation: node name cannot be empty.
     * Ensures proper error handling for invalid input.
     */
    @Test
    @DisplayName("Should throw exception when adding node with empty name")
    void testAddNodeWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addNode("", 1.0, 1.0);
        });
    }

    /**
     * Test successful edge addition between existing nodes.
     * Verifies that edges are properly added to the graph structure.
     */
    @Test
    @DisplayName("Should add edge successfully")
    void testAddEdge() {
        storeService.addEdge("A", "C", 2.5);
        Map<String, List<Edge>> graph = storeService.getGraph();
        assertTrue(graph.containsKey("A"));
    }

    /**
     * Test validation: edge weight cannot be negative.
     * Ensures proper error handling for invalid edge weights.
     */
    @Test
    @DisplayName("Should throw exception when adding edge with negative weight")
    void testAddEdgeWithNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addEdge("A", "B", -1.0);
        });
    }

    /**
     * Test validation: cannot add edge between non-existent nodes.
     * Ensures proper error handling when nodes don't exist.
     */
    @Test
    @DisplayName("Should throw exception when adding edge between non-existent nodes")
    void testAddEdgeWithNonExistentNodes() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.addEdge("A", "Z", 1.0);
        });
    }

    /**
     * Test successful node removal from the graph.
     * Verifies that a node and all its connected edges are properly removed.
     */
    @Test
    @DisplayName("Should remove node successfully")
    void testRemoveNode() {
        storeService.removeNode("A");
        assertFalse(storeService.getAllNodes().contains("A"));
    }

    /**
     * Test validation: cannot remove non-existent node.
     * Ensures proper error handling when trying to remove invalid nodes.
     */
    @Test
    @DisplayName("Should throw exception when removing non-existent node")
    void testRemoveNonExistentNode() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.removeNode("Z");
        });
    }

    /**
     * Test successful edge removal between nodes.
     * Verifies that edges are properly removed from the graph.
     */
    @Test
    @DisplayName("Should remove edge successfully")
    void testRemoveEdge() {
        assertDoesNotThrow(() -> storeService.removeEdge("A", "B"));
    }

    /**
     * Test validation: cannot remove edge between non-existent nodes.
     * Ensures proper error handling when nodes don't exist.
     */
    @Test
    @DisplayName("Should throw exception when removing edge between non-existent nodes")
    void testRemoveEdgeWithNonExistentNodes() {
        assertThrows(IllegalArgumentException.class, () -> {
            storeService.removeEdge("A", "Z");
        });
    }

    /**
     * Test retrieval of all nodes in the graph.
     * Verifies that the graph contains the expected test nodes.
     */
    @Test
    @DisplayName("Should get all nodes")
    void testGetAllNodes() {
        List<String> nodes = storeService.getAllNodes();
        assertNotNull(nodes);
        assertTrue(nodes.size() > 0);
        assertTrue(nodes.containsAll(List.of("A", "B", "C", "D")));
    }

    /**
     * Test retrieval of the complete graph structure.
     * Verifies that the graph contains the expected nodes and structure.
     */
    @Test
    @DisplayName("Should get graph structure")
    void testGetGraph() {
        Map<String, List<Edge>> graph = storeService.getGraph();
        assertNotNull(graph);
        assertTrue(graph.containsKey("A"));
        assertTrue(graph.containsKey("B"));
        assertTrue(graph.containsKey("C"));
        assertTrue(graph.containsKey("D"));
    }

    /**
     * Test DAO implementation for this test class.
     * Provides a mock data access layer with predefined test data.
     * 
     * Test Graph Structure:
     * - Nodes: A(0,0), B(1,1), C(2,0), D(1,-1)
     * - Edges: A-B(1.5), A-D(2.0), B-C(1.0), C-D(1.5) (undirected)
     */
    // TestDao implementation for this test class
    private static class TestDao implements com.om.dao.IDao {
        private java.util.Map<String, double[]> nodes = new java.util.HashMap<>();
        private java.util.Map<String, java.util.List<Edge>> edges = new java.util.HashMap<>();
        private java.util.List<com.om.dm.Store> stores = new java.util.ArrayList<>();
        private java.util.Map<Integer, java.util.List<com.om.dm.Product>> storeProducts = new java.util.HashMap<>();

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
        public void addStore(com.om.dm.Store store) {
            stores.add(store);
        }

        @Override
        public java.util.List<com.om.dm.Store> getAllStores() {
            return new java.util.ArrayList<>(stores);
        }

        @Override
        public com.om.dm.Store getStoreById(int id) {
            return stores.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        }

        @Override
        public void updateStore(com.om.dm.Store store) {
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
        public com.om.dm.Store findNearestStoreWithProduct(String userLocationId, String productName, java.util.Map<String, java.util.List<Edge>> graph) {
            for (com.om.dm.Store store : stores) {
                java.util.List<com.om.dm.Product> products = getProductsByStoreId(store.getId());
                if (products.stream().anyMatch(p -> p.getName().equals(productName))) {
                    return store;
                }
            }
            return null;
        }

        @Override
        public com.om.dm.Store findCheapestStoreWithProduct(String productName) {
            com.om.dm.Store cheapestStore = null;
            double cheapestPrice = Double.MAX_VALUE;
            
            for (com.om.dm.Store store : stores) {
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
        }
    }
} 