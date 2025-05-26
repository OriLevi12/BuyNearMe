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
            store.addProduct(product);
            stores.put(storeId, store);
            saveStoresToFile(stores);
        }
    }

    @Override
    public void removeProductFromStore(int storeId, int productId) {
        Map<Integer, Store> stores = loadStoresFromFile();
        Store store = stores.get(storeId);
        if (store != null) {
            store.removeProductById(productId);
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
    public void clearAll() {
        saveStoresToFile(new HashMap<>());
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

}