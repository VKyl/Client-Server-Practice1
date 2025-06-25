package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.example.Main;
import org.example.http.handlers.auth.JWTHandler;
import org.example.http.handlers.product.ProductGetDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductTest {
    private HttpServer server = null;
    private String token = null;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        server = Main.startServer();
        HttpURLConnection connection = connection();
        token = connection.getHeaderField("Token");
        System.out.println(
                "Login response code " +
                connection.getResponseCode() +
                "\nTOKEN:\n" + token + " \nTOKEN LOGIN:\n" +
                JWTHandler.decode(token)
        );
    }

    @Test
    @SneakyThrows
    void test_get_product() {
        String productId = "1";
        URL url = new URL("http://localhost:8080/api/product/" + productId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }

        Assertions.assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    @SneakyThrows
    void test_get_product_404() {
        String productId = "123";
        URL url = new java.net.URL("http://localhost:8080/api/product/" + productId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }

        Assertions.assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());
    }

    @Test
    @SneakyThrows
    void test_post_product() {
        URL url = new URL("http://localhost:8080/api/product");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }


        try (OutputStream outputStream = connection.getOutputStream()) {
            String productPostJson = """
                    {
                        "id": 0,
                        "name": "Test Product",
                        "price": 49.99
                    }
                    """;
            outputStream.write(productPostJson.getBytes());
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_CREATED, responseCode);

        try (InputStream inputStream = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String response = reader.lines().reduce("", (a, b) -> a + b);
            ObjectMapper objectMapper = new ObjectMapper();
            ProductGetDto product = objectMapper.readValue(response, ProductGetDto.class);
            Assertions.assertEquals(new ProductGetDto(0, "Test Product", 49.99f), product);
        }
    }

    @Test
    @SneakyThrows
    void test_post_product_throws() {
        URL url = new URL("http://localhost:8080/api/product");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }


        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write("".getBytes());
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, responseCode);
    }

    @Test
    @SneakyThrows
    void test_put_product() {
        URL url = new URL("http://localhost:8080/api/product");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }


        try (OutputStream outputStream = connection.getOutputStream()) {
            String productPutJson = """
                    {
                        "name": "Test Product",
                        "price": 49.99
                    }
                    """;
            outputStream.write(productPutJson.getBytes());
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_ACCEPTED, responseCode);

        try (InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String response = reader.lines().reduce("", (a, b) -> a + b);
            Assertions.assertEquals(3, Long.parseLong(response));
        }
    }

    @Test
    @SneakyThrows
    void test_put_product_throws() {
        URL url = new URL("http://localhost:8080/api/product");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }


        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write("".getBytes());
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, responseCode);
    }

    @Test
    @SneakyThrows
    void test_delete_product() {
        String productId = "1";
        URL url = new URL("http://localhost:8080/api/product/" + productId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }

        Assertions.assertEquals(HttpURLConnection.HTTP_NO_CONTENT, connection.getResponseCode());
    }

    @Test
    @SneakyThrows
    void test_delete_product_throws() {
        String productId = "123";
        URL url = new URL("http://localhost:8080/api/product/" + productId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("DELETE");

        if (token != null) {
            connection.setRequestProperty("Token", token);
        }

        Assertions.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, connection.getResponseCode());
    }

    @SneakyThrows
    private HttpURLConnection connection() {
        String jsonBody = "{\"login\":\"admin\", \"password\":\"admin\"}";
        byte[] postData = jsonBody.getBytes();

        URL url = new URL("http://localhost:8080/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(postData);
        }

        return connection;
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }
}
