package login;

import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.example.http.handlers.auth.LoginHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;


public class LoginTest {
    private HttpServer server = null;
    @BeforeEach
    @SneakyThrows
    void setUp() {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080), 10);
        server.createContext("/login", new LoginHandler());
        server.start();

    }
    @Test
    @SneakyThrows
    public void testLogin() {
        String jsonBody = "{\"login\":\"admin\", \"password\":\"admin\"}";
        byte[] postData = jsonBody.getBytes();

        URL url = new URL("http://localhost:8080/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (var outputStream = connection.getOutputStream()) {
            outputStream.write(postData);
        }

        int responseCode = connection.getResponseCode();
        Assertions.assertEquals(200, responseCode);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }
}
