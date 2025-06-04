package com.om.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.om.dm.Store;
import com.om.dm.Product;
import com.om.server.Request;
import com.om.server.Response;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SimpleClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final Gson gson = new GsonBuilder().create();
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
                    default -> System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addStore() throws IOException {
        System.out.print("Enter store name: ");
        String name = scanner.nextLine();
        System.out.print("Enter location ID (A, B, or C): ");
        String locationId = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("locationId", locationId);

        sendRequest("store/add", body);
    }

    private static void getStore() throws IOException {
        System.out.print("Enter store ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);

        sendRequest("store/get", body);
    }

    private static void getAllStores() throws IOException {
        sendRequest("store/getAll", new HashMap<>());
    }

    private static void updateStore() throws IOException {
        System.out.print("Enter store ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter new store name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new location ID (A, B, or C): ");
        String locationId = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("name", name);
        body.put("locationId", locationId);

        sendRequest("store/update", body);
    }

    private static void deleteStore() throws IOException {
        System.out.print("Enter store ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("id", id);

        sendRequest("store/delete", body);
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

        sendRequest("store/addProduct", body);
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

        sendRequest("store/removeProduct", body);
    }

    private static void getStoreProducts() throws IOException {
        System.out.print("Enter store ID: ");
        int storeId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Map<String, Object> body = new HashMap<>();
        body.put("storeId", storeId);

        sendRequest("store/getProducts", body);
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

        sendRequest("store/updateProduct", body);
    }

    private static void findNearestStoreWithProduct() throws IOException {
        System.out.print("Enter your location (A, B, or C): ");
        String location = scanner.nextLine();
        System.out.print("Enter product name to find: ");
        String productName = scanner.nextLine();

        Map<String, Object> body = new HashMap<>();
        body.put("location", location);
        body.put("productName", productName);

        sendRequest("store/findNearest", body);
    }

    private static void sendRequest(String action, Map<String, Object> body) throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Prepare headers
            Map<String, String> headers = new HashMap<>();
            headers.put("action", action);

            // Create request object
            Request<Map<String, Object>> request = new Request<>(headers, body);

            // Convert to JSON and send
            String jsonRequest = gson.toJson(request);
            writer.println(jsonRequest);
            writer.println(); // Empty line to signal end of message

            // Read response
            String responseLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responseLine = reader.readLine()) != null) {
                responseBuilder.append(responseLine);
            }

            System.out.println("\n📨 Server Response:");
            String responseJson = responseBuilder.toString();
            System.out.println(responseJson);

            // Pretty print nearest store details if action is 'store/findNearest'
            if (action.equals("store/findNearest")) {
                try {
                    Response<Store> response = gson.fromJson(responseJson, new com.google.gson.reflect.TypeToken<Response<Store>>(){}.getType());
                    if (response.isSuccess() && response.getBody() != null) {
                        System.out.println("\n--- Nearest Store Details ---");
                        System.out.println(response.getBody().toString());
                    } else {
                        System.out.println("No store found or error: " + response.getMessage());
                    }
                } catch (Exception e) {
                    System.out.println("(Could not parse nearest store details)");
                }
            }
        }
    }
}
