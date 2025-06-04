package com.om.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.om.controller.StoreController;
import com.om.dm.Store;
import com.om.dm.Product;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HandleRequest implements Runnable {
    private final Socket clientSocket;
    private final StoreController storeController;
    private final Gson gson;

    public HandleRequest(Socket socket, StoreController controller) {
        this.clientSocket = socket;
        this.storeController = controller;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                jsonBuilder.append(line);
            }

            Request<Map<String, Object>> request = gson.fromJson(jsonBuilder.toString(), Request.class);

            Map<String, String> headers = request.getHeaders();
            if (headers == null || !headers.containsKey("action")) {
                writer.println(gson.toJson(new Response<>(false, "Missing action header", null)));
                return;
            }

            String action = headers.get("action");
            Map<String, Object> body = request.getBody();

            switch (action) {
                // Store operations
                case "store/add" -> {
                    Store store = gson.fromJson(gson.toJson(body), Store.class);
                    storeController.save(store);
                    writer.println(gson.toJson(new Response<>(true, "Store added", null)));
                }
                case "store/delete" -> {
                    Store store = gson.fromJson(gson.toJson(body), Store.class);
                    storeController.delete(store);
                    writer.println(gson.toJson(new Response<>(true, "Store deleted", null)));
                }
                case "store/get" -> {
                    Store store = gson.fromJson(gson.toJson(body), Store.class);
                    Store retrieved = storeController.get(store);
                    writer.println(gson.toJson(new Response<>(true, "Store found", retrieved)));
                }
                case "store/getAll" -> {
                    writer.println(gson.toJson(new Response<>(true, "All stores retrieved", storeController.getAll())));
                }
                case "store/update" -> {
                    Store store = gson.fromJson(gson.toJson(body), Store.class);
                    storeController.update(store);
                    writer.println(gson.toJson(new Response<>(true, "Store updated", null)));
                }

                // Product operations
                case "store/addProduct" -> {
                    int storeId = ((Number) body.get("storeId")).intValue();
                    Product product = gson.fromJson(gson.toJson(body.get("product")), Product.class);
                    storeController.addProduct(storeId, product);
                    writer.println(gson.toJson(new Response<>(true, "Product added to store", null)));
                }
                case "store/removeProduct" -> {
                    int storeId = ((Number) body.get("storeId")).intValue();
                    String productName = (String) body.get("productName");
                    storeController.removeProduct(storeId, productName);
                    writer.println(gson.toJson(new Response<>(true, "Product removed from store", null)));
                }
                case "store/getProducts" -> {
                    int storeId = ((Number) body.get("storeId")).intValue();
                    writer.println(gson.toJson(new Response<>(true, "Products retrieved", storeController.getProducts(storeId))));
                }
                case "store/updateProduct" -> {
                    int storeId = ((Number) body.get("storeId")).intValue();
                    Product product = gson.fromJson(gson.toJson(body.get("product")), Product.class);
                    storeController.updateProduct(storeId, product);
                    writer.println(gson.toJson(new Response<>(true, "Product updated", null)));
                }
                case "store/findNearest" -> {
                    String location = (String) body.get("location");
                    String productName = (String) body.get("productName");
                    Store nearestStore = storeController.findNearestStoreWithProduct(location, productName);
                    writer.println(gson.toJson(new Response<>(true, "Nearest store found", nearestStore)));
                }
                default -> writer.println(gson.toJson(new Response<>(false, "Unknown action: " + action, null)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
