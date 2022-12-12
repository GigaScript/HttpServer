package org.example.handlers;

import org.example.Headers;
import org.example.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try {
            Path requestPath = request.getPath();
            final var mimeType = Files.probeContentType(requestPath);
            final var length = Files.size(requestPath);
            responseStream.write(
                    Headers.getOk(
                            mimeType
                            , length
                    ));
            Files.copy(requestPath, responseStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
