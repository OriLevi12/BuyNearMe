package com.om.dao;

import com.om.algorithm.*;
import com.om.dm.Product;
import com.om.dm.Store;

import java.io.*;
import java.util.*;

public class DaoFileImpl implements IDao {

    private final String FILE_NAME;

    public DaoFileImpl() {
        this.FILE_NAME = "src/main/resources/datasource.txt";
    }

    public DaoFileImpl(String filePath) {
        this.FILE_NAME = filePath;
    }

    @Override
    public void addStore(Store store) {
        Map<Integer, Store> stores = loadStoresFromFile();
        
        // Check for duplicate stores (same name and location)
        boolean isDuplicate = stores.values().stream()
            .anyMatch(existingStore -> 
                existingStore.getName().equals(store.getName()) &&
                existingStore.getLocationId().equals(store.getLocationId()));
        
        if (isDuplicate) {
            throw new IllegalArgumentException("A store with the same name and location already exists");
        }
        
        int newId = stores.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        store.setId(newId);
        stores.put(newId, store);
        saveStoresToFile(stores);
    }

    @Override
    public List<Store> getAllStores() {
        return new ArrayList<>(loadStoresFromFile().values());
    }

    @Override
    public Store getStoreById(int id) {
        return loadStoresFromFile().get(id);
    }

    @Override
    public void updateStore(Store store) {
        Map<Integer, Store> stores = loadStoresFromFile();
        if (stores.containsKey(store.getId())) {
            stores.put(store.getId(), store);
            saveStoresToFile(stores);
        }
    }

    @Override
    public void deleteStore(int id) {
        Map<Integer, Store> stores = loadStoresFromFile();
        if (stores.containsKey(id)) {
            stores.remove(id);
            saveStoresToFile(stores);
        }
    }

    @Override
    public void addProductToStore(int storeId, Product product) {
        Map<Integer, Store> stores = loadStoresFromFile();
        Store store = stores.get(storeId);
        if (store != null) {
            // Generate a unique product ID by finding the max ID across all stores
            int maxProductId = stores.values().stream()
                .flatMap(s -> s.getProducts().stream())
                .mapToInt(Product::getId)
                .max()
                .orElse(0);
            product.setId(maxProductId + 1);
            
            store.addProduct(product);
            stores.put(storeId, store);
            saveStoresToFile(stores);
        }
    }

    @Override
    public void removeProductFromStore(int storeId, String productName) {
        Map<Integer, Store> stores = loadStoresFromFile();
        Store store = stores.get(storeId);
        if (store != null) {
            store.removeProductByName(productName);
            stores.put(storeId, store);
            saveStoresToFile(stores);
        }
    }

    @Override
    public List<Product> getProductsByStoreId(int storeId) {
        Map<Integer, Store> stores = loadStoresFromFile();
        Store store = stores.get(storeId);
        return store != null ? store.getProducts() : new ArrayList<>();
    }

    @Override
    public void updateProductInStore(int storeId, Product product) {
        Map<Integer, Store> stores = loadStoresFromFile();
        Store store = stores.get(storeId);
        if (store != null) {
            List<Product> products = store.getProducts();
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId() == product.getId()) {
                    products.set(i, product);
                    break;
                }
            }
            store.setProducts(products);
            stores.put(storeId, store);
            saveStoresToFile(stores);
        }
    }

    @Override
    public Store findNearestStoreWithProduct(String userLocationId, String productName, Map<String, List<Edge>> graph) {
        // First check if there's a store at the current location with the product
        for (Store store : getAllStores()) {
            if (store.getLocationId().equals(userLocationId) && 
                store.getProducts().stream().anyMatch(p -> p.getName().equalsIgnoreCase(productName))) {
                // Found product at current location - set empty path and zero distance
                store.setPathToStore(Arrays.asList(userLocationId));
                store.setDistanceToStore(0.0);
                return store;  // Return immediately if found at current location
            }
        }

        IAlgoShortestPath algo = new DijkstraAlgoShortestPathImpl();

        // 1. Add a virtual "SINK" node as the common destination
        final String SINK = "SINK";

        // 2. Load the graph into the algorithm
        for (Map.Entry<String, List<Edge>> entry : graph.entrySet()) {
            String from = entry.getKey();
            algo.addNode(from, 0, 0);
            for (Edge edge : entry.getValue()) {
                algo.addNode(edge.getToNode(), 0, 0);
                algo.addEdge(from, edge.getToNode(), edge.getWeight());
            }
        }
        algo.addNode(SINK, 0, 0);

        // 3. Map to track which store is located at which location node
        Map<String, Store> locationToStore = new HashMap<>();

        // 4. Connect all stores that have the desired product to the SINK node
        for (Store store : getAllStores()) {
            if (!store.getLocationId().equals(userLocationId) &&  // Skip current location
                store.getProducts().stream().anyMatch(p -> p.getName().equalsIgnoreCase(productName))) {
                String loc = store.getLocationId();
                algo.addEdge(loc, SINK, 0);
                locationToStore.put(loc, store);
            }
        }

        // 5. Find shortest path from user location to SINK
        PathResult result = algo.findShortestPath(userLocationId, SINK);

        // 6. If no path found, return null
        List<String> path = result.getPath();
        if (path.isEmpty()) {
            return null;
        }

        // 7. Get the store node (second-to-last node in path) and its corresponding store
        String storeNode = path.get(path.size() - 2);
        Store nearestStore = locationToStore.get(storeNode);
        
        // 8. Store the path information in the store object
        if (nearestStore != null) {
            // Remove the SINK node from the path
            path.remove(path.size() - 1);
            nearestStore.setPathToStore(path);
            nearestStore.setDistanceToStore(result.getTotalWeight());
        }
        
        return nearestStore;
    }

    @Override
    public Store findCheapestStoreWithProduct(String productName) {
        Store cheapestStore = null;
        double lowestPrice = Double.MAX_VALUE;

        // Check all stores for the product
        for (Store store : getAllStores()) {
            for (Product product : store.getProducts()) {
                if (product.getName().equalsIgnoreCase(productName)) {
                    if (product.getPrice() < lowestPrice) {
                        lowestPrice = product.getPrice();
                        cheapestStore = store;
                    }
                    break; // Found the product in this store, no need to check other products
                }
            }
        }
        
        return cheapestStore;
    }

    @Override
    public void clearAll() {
        saveStoresToFile(new HashMap<>());
        saveGraphToFile(new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Store> loadStoresFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Map<Integer, Store>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveStoresToFile(Map<Integer, Store> stores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(stores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Graph persistence methods
    private static final String GRAPH_FILE_NAME = "src/main/resources/graph.dat";

    @Override
    public void saveNode(String nodeName, double x, double y) {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, double[]> nodes = (Map<String, double[]>) graphData.getOrDefault("nodes", new HashMap<>());
        nodes.put(nodeName, new double[]{x, y});
        graphData.put("nodes", nodes);
        saveGraphToFile(graphData);
    }

    @Override
    public void removeNode(String nodeName) {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, double[]> nodes = (Map<String, double[]>) graphData.getOrDefault("nodes", new HashMap<>());
        nodes.remove(nodeName);
        graphData.put("nodes", nodes);
        
        // Also remove all edges connected to this node
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> edges = (Map<String, List<Map<String, Object>>>) graphData.getOrDefault("edges", new HashMap<>());
        edges.remove(nodeName);
        edges.values().forEach(edgeList -> edgeList.removeIf(edgeData -> 
            edgeData.get("from").equals(nodeName) || edgeData.get("to").equals(nodeName)));
        graphData.put("edges", edges);
        
        saveGraphToFile(graphData);
    }

    @Override
    public void saveEdge(String from, String to, double weight) {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> edges = (Map<String, List<Map<String, Object>>>) graphData.getOrDefault("edges", new HashMap<>());
        
        edges.putIfAbsent(from, new ArrayList<>());
        edges.putIfAbsent(to, new ArrayList<>());
        
        // Create simple edge data structure
        Map<String, Object> edgeData = new HashMap<>();
        edgeData.put("from", from);
        edgeData.put("to", to);
        edgeData.put("weight", weight);
        
        // Add edge from->to
        edges.get(from).add(edgeData);
        
        // Create reverse edge data structure
        Map<String, Object> reverseEdgeData = new HashMap<>();
        reverseEdgeData.put("from", to);
        reverseEdgeData.put("to", from);
        reverseEdgeData.put("weight", weight);
        
        // Add edge to->from (undirected graph)
        edges.get(to).add(reverseEdgeData);
        
        graphData.put("edges", edges);
        saveGraphToFile(graphData);
    }

    @Override
    public void removeEdge(String from, String to) {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> edges = (Map<String, List<Map<String, Object>>>) graphData.getOrDefault("edges", new HashMap<>());
        
        if (edges.containsKey(from)) {
            edges.get(from).removeIf(edgeData -> 
                edgeData.get("to").equals(to));
        }
        if (edges.containsKey(to)) {
            edges.get(to).removeIf(edgeData -> 
                edgeData.get("to").equals(from));
        }
        
        graphData.put("edges", edges);
        saveGraphToFile(graphData);
    }

    @Override
    public List<String> getAllNodes() {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, double[]> nodes = (Map<String, double[]>) graphData.getOrDefault("nodes", new HashMap<>());
        return new ArrayList<>(nodes.keySet());
    }

    @Override
    public Map<String, double[]> getAllNodesWithCoordinates() {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, double[]> nodes = (Map<String, double[]>) graphData.getOrDefault("nodes", new HashMap<>());
        System.out.println("DAO: Found " + nodes.size() + " nodes in file");
        return new HashMap<>(nodes);
    }

    @Override
    public Map<String, List<Edge>> getAllEdges() {
        Map<String, Object> graphData = loadGraphFromFile();
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> edges = (Map<String, List<Map<String, Object>>>) graphData.getOrDefault("edges", new HashMap<>());
        
        System.out.println("DAO: Found " + edges.size() + " edge entries in file");
        
        // Convert back to Edge objects
        Map<String, List<Edge>> result = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : edges.entrySet()) {
            String nodeName = entry.getKey();
            List<Edge> edgeList = new ArrayList<>();
            
            for (Map<String, Object> edgeData : entry.getValue()) {
                String from = (String) edgeData.get("from");
                String to = (String) edgeData.get("to");
                double weight = ((Number) edgeData.get("weight")).doubleValue();
                edgeList.add(new Edge(from, to, weight));
            }
            
            result.put(nodeName, edgeList);
            System.out.println("DAO: Node " + nodeName + " has " + edgeList.size() + " edges");
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadGraphFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GRAPH_FILE_NAME))) {
            return (Map<String, Object>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveGraphToFile(Map<String, Object> graphData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GRAPH_FILE_NAME))) {
            oos.writeObject(graphData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}