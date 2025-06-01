package com.om.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.om.controller.StoreController;
import com.om.dm.Store;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HandleRequest implements Runnable {

    private final Socket clientSocket;
    private final StoreController storeController;
    private final Gson gson = new GsonBuilder().create();

    public HandleRequest(Socket clientSocket, StoreController storeController) {
        this.clientSocket = clientSocket;
        this.storeController = storeController;
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


            Request<Store> request = gson.fromJson(jsonBuilder.toString(), Request.class);

            Map<String, String> headers = request.getHeaders();
            if (headers == null || !headers.containsKey("action")) {
                writer.println(gson.toJson(new Response<>(false, "Missing action header", null)));
                return;
            }

            String action = headers.get("action");
            Store store = gson.fromJson(gson.toJson(request.getBody()), Store.class);

            switch (action) {
                case "store/add":
                    storeController.save(store);
                    writer.println(gson.toJson(new Response<>(true, "Store added", null)));
                    break;

                case "store/delete":
                    storeController.delete(store);
                    writer.println(gson.toJson(new Response<>(true, "Store deleted", null)));
                    break;

                case "store/get":
                    Store retrieved = storeController.get(store);
                    writer.println(gson.toJson(new Response<>(true, "Store found", retrieved)));
                    break;

                case "store/getAll":
                    writer.println(gson.toJson(new Response<>(true, "All stores retrieved", storeController.getAll())));
                    break;

                case "store/update":
                    storeController.update(store);
                    writer.println(gson.toJson(new Response<>(true, "Store updated", null)));
                    break;

                default:
                    writer.println(gson.toJson(new Response<>(false, "Unknown action: " + action, null)));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
