package org.example;

public class Headers {
    public static byte[] getNotFound() {
        return ("HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n")
                .getBytes();
    }

    public static byte[] getBadRequest() {
        return ("HTTP/1.1 400 Bad Request\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n")
                .getBytes();
    }

    public static byte[] getOk(String mimeType, long contentLength) {
        return ("HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Connection: close\r\n" +
                "\r\n")
                .getBytes();
    }
}
