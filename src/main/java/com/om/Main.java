package com.om;

import com.om.server.Server;

public class Main {
    public static void main(String[] args) {
        int port = 12345; // Default port
        new Thread(new Server()).start();
    }
}
