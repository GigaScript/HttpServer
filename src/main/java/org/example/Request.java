package org.example;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Request {
    String publicDir = "./public";
    private final String method;
    private final Path path;
    private final String fileName;
    private final String body;
    private final Map<String, String> queryParams;

    public Request(String requestLine) throws IOException {
        String[] request = requestLine.split(" ");
        checkCorrectnessRequest(request);
        this.method = request[0];
        this.path = parsePath(request[1]);
        this.fileName = path.getFileName().toString();
        String queryString = parseQueryString(request[1]);
        this.queryParams = parseParams(queryString);
        this.body = request[2];
    }

    private Path parsePath(String urlFromQuery)  {
        if (urlFromQuery.contains("?")) {
            String[] urlWhitQuery = urlFromQuery.split("\\?");
            urlFromQuery = urlWhitQuery[0];
        }
        return Paths.get(publicDir + urlFromQuery);
    }

    private Map<String, String> parseParams(String queryString) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        String[] paramsArr;
        if (queryString.contains("&")) {
            paramsArr = queryString.split("&");
        } else {
            paramsArr = new String[1];
            paramsArr[0] = queryString;
        }
        for (String pair : paramsArr) {
            String param, value;
            if (pair.contains("=")) {
                final int separatorPosition = pair.indexOf("=");
                final int pairLength = pair.length();
                param = pair.substring(0, separatorPosition);
                if ((separatorPosition + 1) == pairLength) {
                    value = "";
                } else {
                    value = pair.substring(separatorPosition + 1, pairLength);
                }
            } else {
                param = pair;
                value = "";
            }
            queryParams.put(param, value);
        }
        return queryParams;
    }

    private String parseQueryString(String urlFromQuery) {
        String queryString = "null";
        if (urlFromQuery.contains("?")) {
            String[] urlWhitQuery = urlFromQuery.split("\\?");
            queryString = urlWhitQuery[1];
        }
        return queryString;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Path getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
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
