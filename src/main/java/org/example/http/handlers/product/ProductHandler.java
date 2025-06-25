package org.example.http.handlers.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductHandler implements HttpHandler {
    private final List<ProductGetDto> products = new ArrayList<>();

    {
       addProduct(new ProductPutDto("apple", 23.3F));
       addProduct(new ProductPutDto("orange", 25.3F));
       addProduct(new ProductPutDto("pear", 24.3F));
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestHeaders().getFirst("Token") == null) {
            httpExchange.sendResponseHeaders(401, 0);
            httpExchange.close();
            return;
        }
        try {
            switch (httpExchange.getRequestMethod()) {
                case "GET" -> handleGet(httpExchange);
                case "POST" -> handlePost(httpExchange);
                case "PUT" -> handlePut(httpExchange);
                case "DELETE" -> handleDelete(httpExchange);
                default -> handleDefault(httpExchange);
            }
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(500, 0);
            httpExchange.close();
        }
        httpExchange.close();
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        String[] fragments = String.valueOf(httpExchange.getRequestURI().getPath()).split("/");
        try {
            long productId = Long.parseLong(fragments[fragments.length - 1]);
            ProductGetDto product = products.stream().filter(p -> p.id() == productId).findFirst().orElse(null);
            if (product == null) {
                System.out.println("No product with id " + productId + " found");
                httpExchange.sendResponseHeaders(404, 0);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] value = objectMapper.writeValueAsBytes(product);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, value.length);
            httpExchange.getResponseBody().write(value);
        } catch (NumberFormatException e) {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }
    private void handlePost(HttpExchange httpExchange) throws IOException {
        try {
            ProductGetDto productDto = getGetProduct(httpExchange);
            products.set((int) productDto.id(), productDto);
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] value = objectMapper.writeValueAsBytes(productDto);
            httpExchange.sendResponseHeaders(201, value.length);
            httpExchange.getResponseBody().write(value);
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }
    private void handlePut(HttpExchange httpExchange) throws IOException {
        try {
            long id = addProduct(getPutProduct(httpExchange));
            httpExchange.sendResponseHeaders(202, 0);
            httpExchange.getResponseBody().write(new ObjectMapper().writeValueAsBytes(id));
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }
    private void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] fragments = String.valueOf(httpExchange.getRequestURI().getPath()).split("/");
        try {
            long productId = Long.parseLong(fragments[fragments.length - 1]);
            products.remove((int)productId);
            httpExchange.sendResponseHeaders(204, 0);
        } catch (Exception e) {
            httpExchange.sendResponseHeaders(400, 0);
        }
    }
    private void handleDefault(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(405, 0);
    }

    private long addProduct(ProductPutDto productPutDto) {
        products.add(new ProductGetDto(generateProductId(), productPutDto.name(), productPutDto.price()));
        return products.size() - 1;
    }

    private long generateProductId() {
        return products.toArray().length;
    }

    private ProductPutDto getPutProduct(HttpExchange httpExchange) throws IOException {
        return new ObjectMapper().readValue(httpExchange.getRequestBody(), ProductPutDto.class);
    }

    private ProductGetDto getGetProduct(HttpExchange httpExchange) throws IOException {
        return new ObjectMapper().readValue(httpExchange.getRequestBody(), ProductGetDto.class);
    }
}
