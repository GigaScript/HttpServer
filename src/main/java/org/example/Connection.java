package org.example;

import org.example.handlers.Handler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Connection implements Runnable {
    Socket socket = null;
    BufferedInputStream inputStream = null;
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


    @Override
    public void run() {
        try {
            final int requestLimit = 4096;
            inputStream.mark(requestLimit);
            final byte[] buffer = new byte[requestLimit];
            final Optional<Request> request = buildRequest(buffer);
            if (request.isEmpty()) {
                badRequest(outputStream);
                return;
            }
            System.out.println(request.get());
            System.out.println(request.get().getQueryParam("value"));
            Handler handler = server.getHandler(request.get());
            handler.handle(request.get(), outputStream);
            sentAnswer();
            socket.close();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Request> buildRequest(byte[] buffer) throws IOException, URISyntaxException {
        final int requestLength = inputStream.read(buffer);
        final int requestLineEnd = findRequestLineEnd(buffer, requestLength);
        if (requestLineEnd == -1) {
            return Optional.empty();
        }
        String[] requestLine = readRequestLine(buffer, requestLineEnd);
        if (requestLine.length != 3) {
            return Optional.empty();
        }
        final String method = requestLine[0];
        if (!server.getAllowedMethod().contains(method)) {
            return Optional.empty();
        }
        final String path = requestLine[1];
        if (!path.startsWith("/")) {
            return Optional.empty();
        }
        final byte[] headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final List<String> headers = getHeaders(buffer, headersDelimiter, requestLength);
        if (headers.isEmpty()) {
            badRequest(outputStream);
            return Optional.empty();
        }
        final String body = getBody(method, headersDelimiter, headers);
        return Optional.of(new Request(method, path, headers, body));
    }

    private String getBody(String method, byte[] headersDelimiter, List<String> headers) throws IOException {
        String body = "";
        if (!method.equals("GET")) {
            inputStream.skip(headersDelimiter.length);
            final Optional<String> contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                final int length = Integer.parseInt(contentLength.get());
                final byte[] bodyBytes = inputStream.readNBytes(length);
                body = new String(bodyBytes);
            }

        }
        return body;
    }

    private Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private List<String> getHeaders(byte[] buffer, byte[] headersDelimiter, int requestLength) throws IOException {
        final int requestLineEnd = findRequestLineEnd(buffer, requestLength);
        final int headersStart = requestLineEnd + (headersDelimiter.length / 2);
        final int headersEnd = indexOf(buffer, headersDelimiter, headersStart, requestLength);

        if (headersEnd == -1) {
            return new ArrayList<>();
        }
        inputStream.reset();
        inputStream.skip(headersStart);
        final byte[] headersByte = inputStream.readNBytes(headersEnd - headersStart);
        final List<String> header = Arrays.asList(new String(headersByte).split("\r\n"));
        return header;
    }

    private String[] readRequestLine(byte[] buffer, int requestLineEnd) {
        return new String(
                Arrays.copyOf(
                        buffer
                        , requestLineEnd)
        ).split(" ");
    }

    private int findRequestLineEnd(byte[] buffer, int read) throws IOException {
        final byte[] requestLineDelimiter = new byte[]{'\r', '\n'};
        return indexOf(buffer, requestLineDelimiter, 0, read);
    }

    private void badRequest(BufferedOutputStream outputStream) throws IOException {
        try {
            outputStream.write(
                    Headers.getBadRequest()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sentAnswer();
        socket.close();
    }

    public static int indexOf(byte[] buffer, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (buffer[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    private static Socket waitConnect(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    private BufferedInputStream buildInputStream(Socket socket) throws IOException {
        return new BufferedInputStream(
                socket.getInputStream()
        );
    }

    private BufferedOutputStream buildOutputStream(Socket socket) throws IOException {
        return new BufferedOutputStream(
                socket.getOutputStream()
        );
    }

    private void sentAnswer() throws IOException {
        outputStream.flush();
    }
}
