package org.example.server;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServerTest {

    @Test
    @SneakyThrows
    void testTCP() {
        Server server = new Server(8080);

        new Thread() {
            @Override
            @SneakyThrows
            public void run() {
                Thread.sleep(200);
                CommunicationTCP clientTcp = new CommunicationTCP(new Socket(InetAddress.getLocalHost(), 8080));
                clientTcp.write("Hello!");
            }
        }.start();

        ICommunication serverTcp = server.listen();
        assertEquals("Hello!", serverTcp.read());
    }

    @Test
    @SneakyThrows
    void testUDP_THROWS_TIMEOUT() {
        Server server = new Server(8080, InetAddress.getLocalHost(), ":PREFIX:", 1);
        CommunicationUDP clientUdp = new CommunicationUDP(InetAddress.getLocalHost(), 8081, ":PREFIX:", 1);
        new Thread(){
            @Override
            @SneakyThrows
            public void run() {
                Thread.sleep(500);
                assertThrows(SocketTimeoutException.class, () -> clientUdp.write("Hello!"));
            }
        }.start();


    }
}