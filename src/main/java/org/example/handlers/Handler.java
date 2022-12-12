package org.example.handlers;

import org.example.Request;

import java.io.BufferedOutputStream;

@FunctionalInterface
    public interface Handler {
        void handle(Request request, BufferedOutputStream responseStream);
    }
