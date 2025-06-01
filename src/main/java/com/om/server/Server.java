package com.om.server;

import com.om.controller.StoreController;
import com.om.dao.DaoFileImpl;
import com.om.service.StoreService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        StoreService storeService = new StoreService(new DaoFileImpl());
        StoreController controller = new StoreController(storeService);

        // 📍 Add some predefined nodes (so client can refer to them)
        storeService.addNode("A", 0, 0);
        storeService.addNode("B", 1, 0);
        storeService.addNode("C", 2, 2);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🚀 Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(" New client connected");

                new Thread(new HandleRequest(clientSocket, controller)).start();
            }

        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
