package org.example;

import org.example.handlers.Handler;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int maxThread = 64;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(maxThread);
    private int serverPort;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public Server() {
    }

    private void runServer() {
        try (ServerSocket serverSocket = buildServerSocket()) {
            while (!serverSocket.isClosed()) {
                Connection connection = new Connection(serverSocket, this);
                threadPool.execute(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    private ServerSocket buildServerSocket() throws IOException {
        return new ServerSocket(serverPort);
    }

    public void listen(int serverPort) {
        this.serverPort = serverPort;
        runServer();
    }

    public void addHandler(String requestMethod, String requestPath, Handler handler) {
        Map<String, Handler> handlerMap = new ConcurrentHashMap<>();
        if (handlers.containsKey(requestMethod)) {
            handlerMap = handlers.get(requestMethod);
        }
        handlerMap.put(requestPath, handler);
        handlers.put(requestMethod, handlerMap);
    }

    private boolean isValidPath(Path path) {
        String checkedPath = path.getParent()
                + "\\"
                + path.getFileName().toString();
        File file = new File(checkedPath);
        return !file.exists() && !file.isDirectory();
    }

    public synchronized Handler getHandler(Request request) {
        Path path = request.getPath();
        request.getQueryParam("name");
        request.getQueryParam("value");
        if (isValidPath(path)) {
            return handlers.get("HANDLER").get("NOTFOUND");
        }
        String stringPath = path.toString();
        String requestMethod = request.getMethod();
        Map<String, Handler> methodHandlers = handlers.get(requestMethod);
        Handler handler;
        if (methodHandlers != null && methodHandlers.containsKey(stringPath)) {
            handler = methodHandlers.get(stringPath);
        } else {
            handler = handlers.get("HANDLER").get("DEFAULT");
        }
        System.out.println(request);
        return handler;
    }
}