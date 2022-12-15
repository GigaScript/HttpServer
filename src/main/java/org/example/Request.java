package org.example;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

public class Request {
    private final List<String> headers;
    String publicDir = "./public";
    private final String method;
    private final Path path;
    private final String fileName;
    private final URIBuilder builder;
    private final String body;
    private final List<NameValuePair> queryParams;


    public Request(String method, String fullPath, List<String> headers, String body) throws URISyntaxException {
        this.method = method;
        this.builder = new URIBuilder(fullPath);
        this.path = Path.of(publicDir + builder.getPath());
        this.fileName = path.getFileName().toString();
        this.queryParams = builder.getQueryParams();
        this.headers = headers;
        this.body = body;
    }


    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public NameValuePair getQueryParam(String name) {
        if (queryParams.isEmpty()) {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam пуст \r\n");
            return null;
        }
        NameValuePair nameValuePair = builder.getFirstQueryParam(name);
        System.out.println();
        if (nameValuePair != null) {
            System.out.print("[Запрошен QueryParam = " + name + "] Param value = " + nameValuePair.getValue() + "\r\n");
        } else {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam не найден \r\n");
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

    @Override
    public String toString() {
        return "[Request{" +
                "method='" + method + '\'' +
                ", path='" + path.toString() + '\'' +
                ", \n headers='" + headers + '\'' +
                ", \n body='" + body + '\'' +
                ", queryParams=" + getQueryParams() +
                "}]";
    }
}
