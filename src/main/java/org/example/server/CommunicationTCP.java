package org.example.server;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;

public class CommunicationTCP implements Closeable, ICommunication{
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Socket socket;
    @SneakyThrows
    CommunicationTCP(Socket s) {
       socket = s;
       reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
       writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }
    public String read() throws IOException {
        return reader.readLine();
    }
    public void write(String message) throws IOException {
        writer.println(message);
    }
    @Override
    public void close() throws IOException {
        socket.close();
    }
}
