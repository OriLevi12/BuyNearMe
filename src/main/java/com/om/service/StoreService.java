package com.om.service;

import com.om.algorithm.Edge;
import com.om.algorithm.IAlgoShortestPath;
import com.om.algorithm.DijkstraAlgoShortestPathImpl;
import com.om.algorithm.AStarAlgoShortestPathImpl;
import com.om.dao.IDao;
import com.om.dm.Product;
import com.om.dm.Store;

import java.util.*;

public class StoreService {
    private final IDao dao;
    private IAlgoShortestPath algorithm;
    private final Map<String, Point2D> nodeCoordinates;
    private final Map<String, Set<Edge>> graphStructure;

    public StoreService(IDao dao) {
        this(dao, new DijkstraAlgoShortestPathImpl());
    }

    public StoreService(IDao dao, IAlgoShortestPath algorithm) {
        this.dao = dao;
        this.algorithm = algorithm;
        this.nodeCoordinates = new HashMap<>();
        this.graphStructure = new HashMap<>();
        loadGraphFromDao();
    }

    // Load graph data from DAO
    private void loadGraphFromDao() {
        // Load nodes with coordinates
        Map<String, double[]> nodesWithCoords = dao.getAllNodesWithCoordinates();
        Map<String, List<Edge>> edges = dao.getAllEdges();
        
        // Add nodes to memory structures with their actual coordinates
        for (Map.Entry<String, double[]> entry : nodesWithCoords.entrySet()) {
            String nodeName = entry.getKey();
            double[] coords = entry.getValue();
            double x = coords[0];
            double y = coords[1];
            
            nodeCoordinates.put(nodeName, new Point2D.Double(x, y));
            graphStructure.put(nodeName, new HashSet<>());
            algorithm.addNode(nodeName, x, y);
        }
        
        // Add edges to memory structures
        for (Map.Entry<String, List<Edge>> entry : edges.entrySet()) {
            String fromNode = entry.getKey();
            for (Edge edge : entry.getValue()) {
                // Ensure both nodes exist in graphStructure before adding edge
                if (graphStructure.containsKey(edge.getFromNode()) && graphStructure.containsKey(edge.getToNode())) {
                    graphStructure.get(edge.getFromNode()).add(edge);
                    algorithm.addEdge(edge.getFromNode(), edge.getToNode(), edge.getWeight());
                }
            }
        }
    }

    // Add a node (location) with coordinates
    public void addNode(String nodeName, double x, double y) {
        if (nodeName == null || nodeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be null or empty");
        }
        nodeCoordinates.put(nodeName, new Point2D.Double(x, y));
        graphStructure.putIfAbsent(nodeName, new HashSet<>());
        algorithm.addNode(nodeName, x, y);
        dao.saveNode(nodeName, x, y);
    }

    // Add an undirected edge between two nodes
    public void addEdge(String from, String to, double weight) {
        validateNodes(from, to);
        if (weight < 0) {
            throw new IllegalArgumentException("Edge weight cannot be negative");
        }

        Edge edge = new Edge(from, to, weight);
        graphStructure.get(from).add(edge);
        graphStructure.get(to).add(new Edge(to, from, weight));
        algorithm.addEdge(from, to, weight);
        dao.saveEdge(from, to, weight);
    }

    // Remove a node and all its edges
    public void removeNode(String nodeName) {
        if (!graphStructure.containsKey(nodeName)) {
            throw new IllegalArgumentException("Node does not exist: " + nodeName);
        }

        // Remove all edges connected to this node
        for (String otherNode : graphStructure.keySet()) {
            graphStructure.get(otherNode).removeIf(edge -> 
                edge.getToNode().equals(nodeName) || edge.getFromNode().equals(nodeName));
        }

        graphStructure.remove(nodeName);
        nodeCoordinates.remove(nodeName);
        algorithm.removeNode(nodeName);
        dao.removeNode(nodeName);
    }

    // Remove an edge between nodes
    public void removeEdge(String from, String to) {
        validateNodes(from, to);
        graphStructure.get(from).removeIf(edge -> edge.getToNode().equals(to));
        graphStructure.get(to).removeIf(edge -> edge.getToNode().equals(from));
        algorithm.removeEdge(from, to);
        dao.removeEdge(from, to);
    }

    // Add a store at a given location
    public void addStore(String name, String locationId) {
        if (!graphStructure.containsKey(locationId)) {
            throw new IllegalArgumentException("Location node does not exist: " + locationId);
        }

        Store store = new Store();
        store.setName(name);
        store.setLocationId(locationId);
        
        // Set coordinates if available
        Point2D coords = nodeCoordinates.get(locationId);
        if (coords != null) {
            store.setLatitude(coords.getY());
            store.setLongitude(coords.getX());
        }
        
        dao.addStore(store);
    }

    // Product operations
    public void addProductToStore(int storeId, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        dao.addProductToStore(storeId, product);
    }

    public void removeProductFromStore(int storeId, String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        dao.removeProductFromStore(storeId, productName);
    }

    public List<Product> getProductsByStoreId(int storeId) {
        if (storeId <= 0) {
            throw new IllegalArgumentException("Store ID must be positive");
        }
        return dao.getProductsByStoreId(storeId);
    }

    public void updateProductInStore(int storeId, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getId() <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        dao.updateProductInStore(storeId, product);
    }

    public Store findClosestStoreWithProduct(String userLocation, String productName) {
        validateNode(userLocation);
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        return dao.findNearestStoreWithProduct(userLocation, productName, getGraph());
    }

    public Store findCheapestStoreWithProduct(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        return dao.findCheapestStoreWithProduct(productName);
    }

    // Get all stores
    public List<Store> getAllStores() {
        return dao.getAllStores();
    }

    // Get store by ID
    public Store getStoreById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Store ID must be positive");
        }
        return dao.getStoreById(id);
    }

    // Update existing store
    public void updateStore(Store store) {
        if (store == null) {
            throw new IllegalArgumentException("Store cannot be null");
        }
        if (store.getLocationId() != null && !graphStructure.containsKey(store.getLocationId())) {
            throw new IllegalArgumentException("Store location does not exist in graph: " + store.getLocationId());
        }
        dao.updateStore(store);
    }

    // Delete store
    public void deleteStore(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Store ID must be positive");
        }
        Store store = dao.getStoreById(id);
        if (store != null) {
            algorithm.removeNode(store.getLocationId());
            dao.deleteStore(id);
        }
    }

    // Switch to A* algorithm for path finding (better for geographic coordinates)
    public void useAStarAlgorithm() {
        IAlgoShortestPath newAlgo = new AStarAlgoShortestPathImpl();
        copyGraphToNewAlgorithm(newAlgo);
        this.algorithm = newAlgo;
    }

    // Switch to Dijkstra's algorithm (better for abstract graphs)
    public void useDijkstraAlgorithm() {
        IAlgoShortestPath newAlgo = new DijkstraAlgoShortestPathImpl();
        copyGraphToNewAlgorithm(newAlgo);
        this.algorithm = newAlgo;
    }

    private void copyGraphToNewAlgorithm(IAlgoShortestPath newAlgo) {
        // First add all nodes with their coordinates
        for (Map.Entry<String, Point2D> entry : nodeCoordinates.entrySet()) {
            Point2D coord = entry.getValue();
            newAlgo.addNode(entry.getKey(), coord.getX(), coord.getY());
        }

        // Then add all edges
        for (Map.Entry<String, Set<Edge>> entry : graphStructure.entrySet()) {
            for (Edge edge : entry.getValue()) {
                // Only add edges where fromNode is the current node to avoid duplicates
                if (edge.getFromNode().equals(entry.getKey())) {
                    newAlgo.addEdge(edge.getFromNode(), edge.getToNode(), edge.getWeight());
                }
            }
        }
    }

    // Get the current graph structure
    public Map<String, List<Edge>> getGraph() {
        Map<String, List<Edge>> graph = new HashMap<>();
        for (Map.Entry<String, Set<Edge>> entry : graphStructure.entrySet()) {
            graph.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return graph;
    }

    // Get all node names
    public List<String> getAllNodes() {
        return new ArrayList<>(graphStructure.keySet());
    }

    // Validate that both nodes exist
    private void validateNodes(String node1, String node2) {
        validateNode(node1);
        validateNode(node2);
    }

    // Validate that a node exists
    private void validateNode(String node) {
        if (node == null || node.trim().isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be null or empty");
        }
        if (!graphStructure.containsKey(node)) {
            throw new IllegalArgumentException("Node does not exist: " + node);
        }
    }

    // Clear all data and reset the graph
    public void clear() {
        // Clear the DAO data
        dao.clearAll();
        
        // Clear the graph structures
        nodeCoordinates.clear();
        graphStructure.clear();
        
        // Reset the algorithm
        this.algorithm = new DijkstraAlgoShortestPathImpl();
    }

    // Inner class to represent 2D coordinates
    private static class Point2D {
        private final double x;
        private final double y;

        private Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public double getY() { return y; }

        public static class Double extends Point2D {
            public Double(double x, double y) {
                super(x, y);
            }
        }
    }
}
