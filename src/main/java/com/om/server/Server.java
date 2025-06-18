package com.om.server;

import com.om.controller.StoreController;
import com.om.dao.DaoFileImpl;
import com.om.service.StoreService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private static final int PORT = 12345;

    @Override
    public void run() {
        StoreService storeService = new StoreService(new DaoFileImpl());
        StoreController controller = new StoreController(storeService);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new Thread(new HandleRequest(clientSocket, controller)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
