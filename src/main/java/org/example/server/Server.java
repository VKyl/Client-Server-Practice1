package org.example.server;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {
    private ICommunication communication;
    private final int port;
    private final ServerSocket tcpSocket;
    private final boolean isTCP;

    @SneakyThrows
    Server(int port) {
        this.port = port;
        this.tcpSocket = new ServerSocket(port);
        this.communication = null;
        this.isTCP = true;
    }

    @SneakyThrows
    Server(int port, InetAddress address, String expectedPrefix, int retries) {
        this.port = port;
        this.tcpSocket = null;
        this.communication = new CommunicationUDP(address, port, expectedPrefix, retries);
        this.isTCP = false;
    }

    @SneakyThrows
    Server(int port, InetAddress address, String expectedPrefix) {
        this.port = port;
        this.tcpSocket = null;
        this.communication = new CommunicationUDP(address, port, expectedPrefix);
        this.isTCP = false;
    }

    @SneakyThrows
    public ICommunication listen() {
        if (isTCP) {
            communication = new CommunicationTCP(tcpSocket.accept());
        }
        return communication;
    }

    public void start() {
        try {
            System.out.println(communication.read());
            communication.write("ok");
            communication.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
