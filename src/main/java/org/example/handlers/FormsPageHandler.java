package org.example.handlers;

import org.example.Request;
import org.example.handlers.DefaultHandler;
import org.example.handlers.Handler;

import java.io.BufferedOutputStream;
public class FormsPageHandler implements Handler {
    @Override
    public void handle(Request request, BufferedOutputStream responseStream) {
        System.out.println(request.getQueryParams());
        Handler handler = new DefaultHandler();
        handler.handle(request, responseStream);
    }
}
