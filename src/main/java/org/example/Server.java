package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int maxThread = 64;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(maxThread);

    public Server() {
        final var validPaths = getValidPaths();
        try (ServerSocket serverSocket = buildServerSocket()) {
            while (!serverSocket.isClosed()) {
                Connection connection = new Connection(serverSocket, validPaths);
                threadPool.execute(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private ServerSocket buildServerSocket() throws IOException {
        int listenPort = 9999;
        return new ServerSocket(listenPort);
    }



    private static List<String> getValidPaths() {
        return List.of("/index.html", "/spring.svg", "/media/spring.png", "/resources.html", "/resources/css/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    }
}