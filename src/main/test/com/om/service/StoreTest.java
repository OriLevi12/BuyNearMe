package com.om.service;

import com.om.dao.DaoFileImpl;
import com.om.dm.Product;
import com.om.dm.Store;

public class StoreTest {
    public static void main(String[] args) {
        // Create a single service instance
        StoreService service = new StoreService(new DaoFileImpl());

        // Demonstrate different features of the system
        demonstrateBasicPathFinding(service);
        
        service.clear(); // Clear before next demo
        demonstrateAlgorithmSwitching(service);
        
        service.clear(); // Clear before next demo
        demonstrateGraphModifications(service);
        
        service.clear(); // Clear before next demo
        demonstrateErrorHandling(service);
    }

    private static void demonstrateBasicPathFinding(StoreService service) {
        System.out.println("\n=== Basic Path Finding Demo ===");
        
        // Initialize the graph with geographic coordinates
        initializeGraph(service);
        addStoresAndProducts(service);

        // Find path to Mouse with default Dijkstra algorithm
        System.out.println("\nFinding path using Dijkstra's algorithm:");
        findNearestStore(service, "A", "Mouse");
    }

    private static void demonstrateAlgorithmSwitching(StoreService service) {
        System.out.println("\n=== Algorithm Switching Demo ===");
        initializeGraph(service);
        addStoresAndProducts(service);

        // Compare Dijkstra vs A* results
        System.out.println("\nDijkstra's algorithm result:");
        findNearestStore(service, "A", "Mouse");

        System.out.println("\nSwitching to A* algorithm (considers geographic coordinates):");
        service.useAStarAlgorithm();
        findNearestStore(service, "A", "Mouse");

        // Switch back to Dijkstra
        System.out.println("\nSwitching back to Dijkstra:");
        service.useDijkstraAlgorithm();
        findNearestStore(service, "A", "Mouse");
    }

    private static void demonstrateGraphModifications(StoreService service) {
        System.out.println("\n=== Graph Modifications Demo ===");
        initializeGraph(service);
        addStoresAndProducts(service);

        // Initial path
        System.out.println("\nInitial path:");
        findNearestStore(service, "A", "Mouse");

        // Remove an edge and find new path
        System.out.println("\nRemoving edge B->C and finding new path:");
        service.removeEdge("B", "C");
        findNearestStore(service, "A", "Mouse");

        // Add a shorter path
        System.out.println("\nAdding shorter path B->X (weight 1.5):");
        service.addEdge("B", "X", 1.5);
        findNearestStore(service, "A", "Mouse");

        // Remove a node and see the effect
        System.out.println("\nRemoving node C and finding new path:");
        service.removeNode("C");
        findNearestStore(service, "A", "Mouse");
    }

    private static void demonstrateErrorHandling(StoreService service) {
        System.out.println("\n=== Error Handling Demo ===");
        initializeGraph(service);

        try {
            System.out.println("Trying to add edge with negative weight:");
            service.addEdge("A", "B", -1);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        try {
            System.out.println("\nTrying to add store at non-existent location:");
            service.addStore("InvalidStore", "NonExistentLocation");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        try {
            System.out.println("\nTrying to find path from non-existent location:");
            service.findClosestStoreWithProduct("NonExistentLocation", "Mouse");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }

    private static void initializeGraph(StoreService service) {
        // Add nodes with actual coordinates (x,y)
        service.addNode("A", 0, 0);      // User location
        service.addNode("B", 2, 0);
        service.addNode("C", 4, 0);      // Store 1
        service.addNode("D", 6, 0);
        service.addNode("X", 4, 2);      // Store 2
        service.addNode("Z", 8, 0);      // Store 3

        // Add edges with distances
        service.addEdge("A", "B", 2);
        service.addEdge("B", "C", 2);
        service.addEdge("C", "D", 2);
        service.addEdge("B", "X", 3);
        service.addEdge("D", "Z", 2);
    }

    private static void addStoresAndProducts(StoreService service) {
        // Add stores at specific locations
        service.addStore("HomeStore", "A");  // id 1 - store at user's location
        service.addStore("TechZone", "C");   // id 2
        service.addStore("QuickBuy", "X");   // id 3
        service.addStore("CheapStuff", "Z"); // id 4

        // Add products to stores
        service.addProductToStore(1, new Product(1, "screen", 29.9));  // Mouse at A (cheapest)
        service.addProductToStore(2, new Product(2, "Mouse", 59.9));  // Mouse at C
        service.addProductToStore(3, new Product(3, "Keyboard", 109.9));  // Keyboard at X
        service.addProductToStore(3, new Product(3, "Mouse", 99.9));  // Mouse at X
        service.addProductToStore(4, new Product(4, "Mouse", 49.9));  // Mouse at Z
    }

    private static void findNearestStore(StoreService service, String userLocation, String productName) {
        try {
            System.out.println("\nSearching for nearest '" + productName + "' from location '" + userLocation + "'");
            Store nearest = service.findClosestStoreWithProduct(userLocation, productName);

            if (nearest != null) {
                System.out.println("✅ Found nearest store:");
                System.out.println("Store: " + nearest.getName());
                System.out.println("Location ID: " + nearest.getLocationId());
                System.out.println("Path: " + String.join(" -> ", nearest.getPathToStore()));
                System.out.println("Distance: " + String.format("%.2f", nearest.getDistanceToStore()) + " units");
                System.out.println("Price: $" + nearest.getProducts().stream()
                        .filter(p -> p.getName().equalsIgnoreCase(productName))
                        .findFirst()
                        .map(Product::getPrice)
                        .orElse(0.0));
            } else {
                System.out.println("❌ No store found with product '" + productName + "'");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error finding store: " + e.getMessage());
        }
    }
}
