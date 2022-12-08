package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Connection implements Runnable {

    Socket socket = null;
    BufferedReader inputStream = null;
    BufferedOutputStream outputStream = null;
    List<String> validPaths;


    public Connection(ServerSocket serverSocket, List<String> validPaths) throws IOException {
        try {
            this.socket = waitConnect(serverSocket);
            this.inputStream = buildInputStream(socket);
            this.outputStream = buildOutputStream(socket);
            this.validPaths = validPaths;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private void closeStream() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            socket.close();
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
            final String requestLine;
            requestLine = receiveRequest();
            final var requestParts = parseRequest(requestLine);
            checkCorrectnessRequest(requestParts);
            final var path = requestParts[1];
            if (isValidPath(path)) {
                addAnswer(Headers.getNotFound());
                sentAnswer();
                socket.close();
            }
            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            if (path.equals("/classic.html")) {
                byte[] changedClassicPage = changeClassicPage(filePath, mimeType);
                addAnswer(
                        Headers.getOk(
                                mimeType
                                , changedClassicPage.length)
                );
                addAnswer(changedClassicPage);
                sentAnswer();
            }
            final var length = Files.size(filePath);
            addAnswer(
                    Headers.getOk(
                            mimeType
                            , length
                    ));
            Files.copy(filePath, outputStream);
            sentAnswer();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private byte[] changeClassicPage(Path filePath, String mimeType) throws IOException {
        final var template = Files.readString(filePath);
        return template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
    }

    private void addAnswer(byte[] content) throws IOException {
        outputStream.write((content));
    }

    private void sentAnswer() throws IOException {
        outputStream.flush();
    }

    private boolean isValidPath(String path) {
        return !validPaths.contains(path);
    }

    private void checkCorrectnessRequest(String[] requestParts) throws IOException {
        if (requestParts.length != 3) {
            socket.close();
        }
    }

    private static String[] parseRequest(String requestLine) {
        return requestLine.split(" ");
    }

    private String receiveRequest() throws IOException {
        return inputStream.readLine();
    }

}
