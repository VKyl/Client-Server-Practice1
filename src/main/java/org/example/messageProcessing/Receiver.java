package org.example.messageProcessing;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.BytesConverter;
import org.example.messages.Message;
import org.example.messages.MessagesType;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class Receiver implements IReceiver, Runnable {
    private BlockingQueue<byte[]> queue;
    private AtomicBoolean isRunning;

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                receiveMessage();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SneakyThrows
    public void receiveMessage() {
        final byte[] message = generateMessage();
        System.out.println("Message received " + BytesConverter.bytesToHex(message));
        queue.put(message);
    }

    private byte[] generateMessage() {
        String senderId = UUID.randomUUID().toString();
        MessagesType messageType = resolveMessageType((int) (Math.random() * 2));
        String productId = UUID.randomUUID().toString();
        Message message = new Message(senderId, messageType, productId);
        System.out.println("Message generated " + messageType);
        return Encryptor.encode(message);
    }

    private MessagesType resolveMessageType(int type) {
        return switch (type) {
            case 0 -> MessagesType.GET_QUANTITY;
            case 1 -> MessagesType.REMOVE_PRODUCT;
            default -> throw new IllegalArgumentException("Unknown message type index " + type);
        };
    }
}
