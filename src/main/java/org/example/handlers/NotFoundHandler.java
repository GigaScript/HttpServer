package org.example.handlers;

import org.example.Headers;
import org.example.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class NotFoundHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try {
            responseStream.write(
                    Headers.getNotFound()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
