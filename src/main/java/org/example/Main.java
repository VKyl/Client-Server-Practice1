package org.example;

import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;
import org.example.http.handlers.auth.LoginHandler;
import org.example.http.handlers.product.ProductHandler;
import org.example.messageProcessing.*;
import org.example.messages.Message;
import org.example.messages.Response;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main {
    public static void main(String[] args) {}

    @SneakyThrows
    public static HttpServer startServer() {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080), 10);
        server.createContext("/login", new LoginHandler());
        server.createContext("/api/product", new ProductHandler());
        server.start();
        return server;
    }

    @SneakyThrows
    public static Thread[] MessagePipeLine(int threadSleepLength) {
        BlockingQueue<byte[]> receiverQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>();
        BlockingQueue<byte[]> encryptorQueue = new LinkedBlockingQueue<>();

        AtomicBoolean isRunning = new AtomicBoolean(true);


        Receiver receiver = new Receiver(receiverQueue, isRunning);
        Decryptor decryptor = new Decryptor(receiverQueue, messageQueue, isRunning);
        Processor processor = new Processor(messageQueue, responseQueue, isRunning);
        Encryptor encryptor = new Encryptor(responseQueue, encryptorQueue, isRunning);
        Sender sender = new Sender(encryptorQueue, isRunning);

        Thread[] threads = {
            new Thread(receiver),
            new Thread(decryptor),
            new Thread(processor),
            new Thread(encryptor),
            new Thread(sender)
        };

        for (Thread thread : threads) thread.start();
        Thread.sleep(threadSleepLength);
        isRunning.set(false);
        for (Thread thread : threads) thread.join();

        return threads;
    }
}