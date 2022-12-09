package org.example;

import org.example.handlers.Handler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements Runnable {
    Socket socket = null;
    BufferedReader inputStream = null;
    BufferedOutputStream outputStream = null;
    private Server server;


    public Connection(ServerSocket serverSocket, Server server) throws IOException {
        try {
            this.socket = waitConnect(serverSocket);
            this.inputStream = buildInputStream(socket);
            this.outputStream = buildOutputStream(socket);
            this.server = server;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private static Socket waitConnect(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    private BufferedReader buildInputStream(Socket socket) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()
                ));
    }

    private BufferedOutputStream buildOutputStream(Socket socket) throws IOException {
        return new BufferedOutputStream(
                socket.getOutputStream()
        );
    }

    @Override
    public void run() {
        try {
            final Request request = new Request(
                    inputStream.readLine()
            );
            Handler handler = server.getHandler(request);
            handler.handle(request, outputStream);
            sentAnswer();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sentAnswer() throws IOException {
        outputStream.flush();
    }
}
