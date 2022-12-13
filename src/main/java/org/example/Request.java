package org.example;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

public class Request {
    String publicDir = "./public";
    private final String method;
    private final Path path;
    private final String fileName;
    private final URIBuilder builder;
    private final String body;
    private final List<NameValuePair> queryParams;

    public Request(String requestLine) throws IOException, URISyntaxException {
        String[] request = requestLine.split(" ");
        checkCorrectnessRequest(request);
        this.method = request[0];
        this.builder = new URIBuilder(request[1]);
        this.path = Path.of(publicDir + builder.getPath());
        this.fileName = path.getFileName().toString();
        this.queryParams = builder.getQueryParams();
        this.body = request[2];
    }


    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public NameValuePair getQueryParam(String name) {
        if (queryParams.isEmpty()) {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam пуст \n\n");
            return null;
        }
        NameValuePair nameValuePair = builder.getFirstQueryParam(name);
        System.out.println();
        if (nameValuePair != null) {
            System.out.print("[Запрошен QueryParam = " + name + "] Param value = " + nameValuePair.getValue() + "\r\n");
        } else {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam не найден \n\n");
        }
        return nameValuePair;
    }

    public Path getPath() {
        return path;
    }


    public String getMethod() {
        return method;
    }


    public String getBody() {
        return body;
    }

    private void checkCorrectnessRequest(String[] requestParts) {
        if (requestParts.length != 3) {
            throw new RuntimeException("Объект Request не корректный");
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path.toString() + '\'' +
                ", body='" + body + '\'' +
                ", queryParams=" + queryParams +
                '}';
    }
}
