package org.example;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
    private final List<NameValuePair> postParams;


    public Request(String method, String fullPath, List<String> headers, String body) throws URISyntaxException {
        this.method = method;
        this.builder = new URIBuilder(fullPath);
        this.path = Path.of(publicDir + builder.getPath());
        this.fileName = path.getFileName().toString();
        this.queryParams = builder.getQueryParams();
        this.headers = headers;
        this.body = body;
        this.postParams = setPostParam();
    }

    private List<NameValuePair> setPostParam() {
        if (!getMethod().equals("GET")) {
            final Optional<String> contentType = getHeader("Content-Type");
            if (contentType.isPresent()) {
                final String contentTypeValue = contentType.get();
                if (contentTypeValue.contains("x-www-form-urlencoded")) {
                    return URLEncodedUtils.parse(getBody(), StandardCharsets.UTF_8);
                }
            }
        }
        return new ArrayList<>();
    }

    public Optional<String> getHeader(String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public NameValuePair getQueryParam(String name) {
        if (queryParams.isEmpty()) {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam пуст \r\n");
            return null;
        }
        NameValuePair nameValuePair = findParamByName(name, queryParams);
        if (nameValuePair != null) {
            System.out.print("[Запрошен QueryParam = " + name + "] Param value = " + nameValuePair.getValue() + "\r\n");
        } else {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam не найден \r\n");
        }
        return nameValuePair;
    }

    public NameValuePair getPostParam(String name) {
        if (postParams.isEmpty()) {
            System.err.print("[Запрошен QueryParam = " + name + "] QueryParam пуст \r\n");
            return null;
        }
        NameValuePair nameValuePair = findParamByName(name, postParams);
        if (nameValuePair != null) {
            System.out.print("[Запрошен PostParam = " + name + "] Param value = " + nameValuePair.getValue() + "\r\n");
        } else {
            System.err.print("[Запрошен PostParam = " + name + "] PostParam не найден \r\n");
        }
        return nameValuePair;
    }

    private NameValuePair findParamByName(String name, List<NameValuePair> params) {
        NameValuePair nameValuePair = null;
        for (NameValuePair param : params) {
            if (param.getName().equals(name)) {
                nameValuePair = param;
            }
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
                ", postParams=" + getPostParams() +
                "}]";
    }
}
