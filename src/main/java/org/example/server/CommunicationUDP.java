package org.example.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;

public class CommunicationUDP implements Closeable, ICommunication {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int RETRIES;
    private final String expPrefix;
    private final int port;
    private final byte[] buffer = new byte[1024];

    public CommunicationUDP(InetAddress address, int port, String expPrefix, int RETRIES) throws SocketException {
        socket = new DatagramSocket(port);
        this.address = address;
        this.port = port;
        this.RETRIES = RETRIES;
        this.expPrefix = expPrefix;
    }

    public CommunicationUDP(InetAddress address, int port, String expPrefix) throws SocketException {
        socket = new DatagramSocket(port);
        this.address = address;
        this.port = port;
        this.RETRIES = 5;
        this.expPrefix = expPrefix;
    }

    public void write(String msg) throws IOException {
        for(int i = 0; i < RETRIES; i++){
            write_("REQ"+ expPrefix + msg);
            String message = read();
            if (message.startsWith("RES" + expPrefix)) return;
        }
    }

    public String read() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(5 * 1000);
        socket.receive(packet);
        byte[] data = packet.getData();
        String response = new String(data, 0, packet.getLength());
        handlePrefixCorrectness(response);
        return response;
    }

    public void close() throws IOException {
        socket.close();
    }

    private void handlePrefixCorrectness(String response) throws IOException {
        if (response.startsWith("REQ" + expPrefix)) {
            String content = response.substring(("REQ" + expPrefix).length());
            write_("RES" + expPrefix + content);
        } else {
            write_(response);
        }
    }


    private void write_(String message) throws IOException {
        byte[] data = (message).getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }
}
