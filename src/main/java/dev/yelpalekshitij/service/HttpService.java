package dev.yelpalekshitij.service;

import dev.yelpalekshitij.exception.ErrorCode;
import dev.yelpalekshitij.exception.ServiceException;
import dev.yelpalekshitij.util.RetryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.*;
import java.util.Map;

public class HttpService {

    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int INITIAL_DELAY_MS = 500;

    private final HttpClient client;

    public HttpService(HttpClient client) {
        this.client = client;
    }

    public HttpResponse<String> get(String url, Map<String, String> headers) {
        return RetryExecutor.execute(MAX_RETRY_ATTEMPTS, INITIAL_DELAY_MS, () -> execute(buildGet(url, headers)));
    }

    public HttpResponse<String> post(String url, Map<String, String> headers, String body) {
        return RetryExecutor.execute(MAX_RETRY_ATTEMPTS, INITIAL_DELAY_MS, () -> execute(buildPost(url, headers, body)));
    }

    private HttpRequest buildGet(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        headers.forEach(builder::header);

        return builder.build();
    }

    private HttpRequest buildPost(String url, Map<String, String> headers, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body));
        headers.forEach(builder::header);

        return builder.build();
    }

    private HttpResponse<String> execute(HttpRequest request) {
        try {
            // logger.debug("HTTP {} {}", request.method(), request.uri());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new ServiceException(ErrorCode.HTTP_FAILURE, "HTTP " + response.statusCode());
            }

            return response;
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.HTTP_FAILURE, e.getMessage());
        }
    }

}
