package org.example.http.handlers.auth;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private final Map<String, String> users = new HashMap<>();

    {
        users.put("admin", "admin");
        users.put("user", "user");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if(!exchange.getRequestMethod().equals("POST")) {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            LoginRequest loginRequest = mapper.readValue(exchange.getRequestBody(), LoginRequest.class);
            if (!isCorrectPassword(loginRequest.login(), loginRequest.password())) {
                exchange.sendResponseHeaders(403, 0);
                exchange.close();
                return;
            }
            exchange.getResponseHeaders().add("Token", JWTHandler.createToken(loginRequest.login()));
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (DatabindException e) {
            System.out.println("Error: " + e.getMessage());
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }

    private boolean isCorrectPassword(final String login, final String password) {
        final String requiredPassword = users.get(login);
        return requiredPassword != null && requiredPassword.equals(password);
    }
}
