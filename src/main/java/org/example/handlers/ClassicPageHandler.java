package org.example.handlers;

import org.example.Headers;
import org.example.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class ClassicPageHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        try {
            Path requestPath = request.getPath();
            final var mimeType = Files.probeContentType(requestPath);
            byte[] changedClassicPage = changeClassicPage(requestPath);
            responseStream.write(
                    Headers.getOk(
                            mimeType
                            , changedClassicPage.length)
            );
            responseStream.write(changedClassicPage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] changeClassicPage(Path requestPath) throws IOException {
        final var template = Files.readString(requestPath);
        return template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
    }
}
