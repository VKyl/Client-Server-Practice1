package org.example.messageProcessing;

import lombok.AllArgsConstructor;
import org.example.BytesConverter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class Sender implements Runnable {
    BlockingQueue<byte[]> response;
    AtomicBoolean isRunning;

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                sendMessage(response.take());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(byte[] message) {
        System.out.println("OutputMessage: " + BytesConverter.bytesToHex(message));
    }
}
