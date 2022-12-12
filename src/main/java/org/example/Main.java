package org.example;

import org.example.handlers.ClassicPageHandler;
import org.example.handlers.DefaultHandler;
import org.example.handlers.FormsPageHandler;
import org.example.handlers.NotFoundHandler;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        server.addHandler("GET", ".\\public\\classic.html", new ClassicPageHandler());
        server.addHandler("GET", ".\\public\\forms.html", new FormsPageHandler());
        server.addHandler("HANDLER", "DEFAULT", new DefaultHandler());
        server.addHandler("HANDLER", "NOTFOUND", new NotFoundHandler());
        server.listen(9999);
    }
}