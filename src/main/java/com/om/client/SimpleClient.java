package com.om.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleClient {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Store Management System ===");
            System.out.println("1. Add Store");
            System.out.println("2. Get Store by ID");
            System.out.println("3. Get All Stores");
            System.out.println("4. Update Store");
            System.out.println("5. Delete Store");
            System.out.println("6. Add Product to Store");
            System.out.println("7. Remove Product from Store");
            System.out.println("8. Get Store Products");
            System.out.println("9. Update Product in Store");
            System.out.println("10. Find Nearest Store with Product");
            System.out.println("11. Find Cheapest Store with Product");
            System.out.println("12. Add Node to Graph");
            System.out.println("13. Add Edge to Graph");
            System.out.println("14. Remove Node from Graph");
            System.out.println("15. Remove Edge from Graph");
            System.out.println("16. Show All Nodes");
            System.out.println("17. Show All Nodes with Coordinates");
            System.out.println("18. Show All Edges");
            System.out.println("19. Clear All Data");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 0) break;

            try {
                switch (choice) {
                    case 1 -> addStore();
                    case 2 -> getStore();
                    case 3 -> getAllStores();
                    case 4 -> updateStore();
                    case 5 -> deleteStore();
                    case 6 -> addProductToStore();
                    case 7 -> removeProductFromStore();
                    case 8 -> getStoreProducts();
                    case 9 -> updateProductInStore();
                    case 10 -> findNearestStoreWithProduct();
                    case 11 -> findCheapestStoreWithProduct();
                    case 12 -> addNode();
                    case 13 -> addEdge();
                    case 14 -> removeNode();
                    case 15 -> removeEdge();
                    case 16 -> showAllNodes();
                    case 17 -> showAllNodesWithCoordinates();
                    case 18 -> showAllEdges();
                    case 19 -> clearAllData();
                    default -> System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addStore() throws IOException {
        // First show available nodes
        System.out.println("\nAvailable locations:");
        showAllNodes();
        
        System.out.print("Enter store name: ");
        String name = scanner.nextLine();
        System.out.print("Enter location ID: ");
        String locationId = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("locationId", locationId);

        NetworkClient.sendRequest("store/add", body);
    }

    private static void getStore() throws IOException {
        System.out.print("Enter store ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);

        NetworkClient.sendRequest("store/get", body);
    }

    private static void getAllStores() throws IOException {
        NetworkClient.sendRequest("store/getAll", new HashMap<>());
    }

    private static void updateStore() throws IOException {
        System.out.print("Enter store ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new store name: ");
        String name = scanner.nextLine();
        
        // Show available nodes
        System.out.println("\nAvailable locations:");
        showAllNodes();
        System.out.print("Enter new location ID: ");
        String locationId = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("name", name);
        body.put("locationId", locationId);

        NetworkClient.sendRequest("store/update", body);
    }

    private static void deleteStore() throws IOException {
        System.out.print("Enter store ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);

        NetworkClient.sendRequest("store/delete", body);
    }

    private static void addProductToStore() throws IOException {
        System.out.print("Enter store ID: ");
        int storeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter product price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", price);

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("product", product);

        NetworkClient.sendRequest("store/addProduct", body);
    }

    private static void removeProductFromStore() throws IOException {
        System.out.print("Enter store ID: ");
        int storeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product name to remove: ");
        String productName = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("productName", productName);

        NetworkClient.sendRequest("store/removeProduct", body);
    }

    private static void getStoreProducts() throws IOException {
        System.out.print("Enter store ID: ");
        int storeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);

        NetworkClient.sendRequest("store/getProducts", body);
    }

    private static void updateProductInStore() throws IOException {
        System.out.print("Enter store ID: ");
        int storeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product ID: ");
        int productId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter new product price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Map<String, Object> product = new HashMap<>();
        product.put("id", productId);
        product.put("name", productName);
        product.put("price", price);

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);
        body.put("product", product);

        NetworkClient.sendRequest("store/updateProduct", body);
    }

    private static void findNearestStoreWithProduct() throws IOException {
        System.out.println("\nAvailable locations:");
        showAllNodes();
        System.out.print("Enter your location: ");
        String location = scanner.nextLine();
        System.out.print("Enter product name to find: ");
        String productName = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("location", location);
        body.put("productName", productName);

        NetworkClient.sendRequest("store/findNearest", body);
    }

    private static void findCheapestStoreWithProduct() throws IOException {
        System.out.print("Enter product name to find: ");
        String productName = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("productName", productName);

        NetworkClient.sendRequest("store/findCheapest", body);
    }

    private static void addNode() throws IOException {
        System.out.print("Enter node name: ");
        String nodeName = scanner.nextLine();
        System.out.print("Enter X coordinate: ");
        double x = scanner.nextDouble();
        System.out.print("Enter Y coordinate: ");
        double y = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("nodeName", nodeName);
        body.put("x", x);
        body.put("y", y);

        NetworkClient.sendRequest("graph/addNode", body);
    }

    private static void addEdge() throws IOException {
        System.out.print("Enter source node: ");
        String from = scanner.nextLine();
        System.out.print("Enter target node: ");
        String to = scanner.nextLine();
        System.out.print("Enter edge weight: ");
        double weight = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("weight", weight);

        NetworkClient.sendRequest("graph/addEdge", body);
    }

    private static void removeNode() throws IOException {
        System.out.print("Enter node name to remove: ");
        String nodeName = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("nodeName", nodeName);

        NetworkClient.sendRequest("graph/removeNode", body);
    }

    private static void showAllNodes() throws IOException {
        NetworkClient.sendRequest("graph/getNodes", new HashMap<>());
    }

    private static void removeEdge() throws IOException {
        System.out.print("Enter source node: ");
        String from = scanner.nextLine();
        System.out.print("Enter target node: ");
        String to = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);

        NetworkClient.sendRequest("graph/removeEdge", body);
    }

    private static void showAllEdges() throws IOException {
        NetworkClient.sendRequest("graph/getEdges", new HashMap<>());
    }

    private static void showAllNodesWithCoordinates() throws IOException {
        NetworkClient.sendRequest("graph/getNodesWithCoordinates", new HashMap<>());
    }

    private static void clearAllData() throws IOException {
        NetworkClient.sendRequest("graph/clearAllData", new HashMap<>());
    }
}
