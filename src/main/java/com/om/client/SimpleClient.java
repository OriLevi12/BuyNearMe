package com.om.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SimpleClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            Gson gson = new GsonBuilder().create();

            // Prepare headers
            Map<String, String> headers = new HashMap<>();
            headers.put("action", "store/add");

            // Prepare body (store details)
            Map<String, Object> body = new HashMap<>();
            body.put("name", "SuperStore");
            body.put("locationId", "A");

            // Build full request
            Map<String, Object> request = new HashMap<>();
            request.put("headers", headers);
            request.put("body", body);

            // Convert to JSON
            String jsonRequest = gson.toJson(request);

            // Send the request
            writer.println(jsonRequest);
            writer.println(); // Important: send empty line to signal end of message

            // Read response
            String responseLine;
            StringBuilder responseBuilder = new StringBuilder();
            while ((responseLine = reader.readLine()) != null) {
                responseBuilder.append(responseLine);
            }

            System.out.println("📨 Server Response:");
            System.out.println(responseBuilder.toString());

        } catch (IOException e) {
            System.err.println("❌ Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
