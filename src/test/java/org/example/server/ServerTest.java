package org.example.server;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

//    @Test
//    @SneakyThrows
//    void testUDP() {
//        Server server = new Server(8080, InetAddress.getLocalHost(), ":PREFIX:");
//        CommunicationUDP clientUdp = new CommunicationUDP(InetAddress.getLocalHost(), 8081, ":PREFIX:");
//        ICommunication serverUdp = server.listen();
//        new Thread(){
//            @Override
//            @SneakyThrows
//            public void run() {
//                Thread.sleep(500);
//                clientUdp.write("Hello!");
//            }
//        }.start();
//
//        assertEquals(serverUdp.read(), "Hello!");
//    }
}