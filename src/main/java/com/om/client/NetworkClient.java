package com.om.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.om.dm.Store;
import com.om.server.Request;
import com.om.server.Response;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;

public class NetworkClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;
    private static final Gson gson = new GsonBuilder().create();

    public static void sendRequest(String action, Map<String, Object> body) throws IOException {
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