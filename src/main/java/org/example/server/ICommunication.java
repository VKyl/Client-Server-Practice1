package org.example.server;

import java.io.IOException;

public interface ICommunication {
    String read() throws IOException;
    void write(String msg) throws IOException;
    void close() throws IOException;
}
