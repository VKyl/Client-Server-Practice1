package org.example.server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName(null);

            try(CommunicationTCP tcp = new CommunicationTCP(new Socket(address, 8080))){
                tcp.write("Hi");
                System.out.println(tcp.read());
            }

        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }
}
