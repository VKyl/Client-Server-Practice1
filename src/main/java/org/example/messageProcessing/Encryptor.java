package org.example.messageProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.Crc16;
import org.example.messages.Message;
import org.example.messages.Response;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class Encryptor implements Runnable {
    BlockingQueue<Response> message;
    BlockingQueue<byte[]> result;
    AtomicBoolean isRunning;

    @Override
    @SneakyThrows
    public void run() {
        try {
            while (isRunning.get()) {
                result.put(encode(message.take()));
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static byte[] encode(Message message){
        ObjectMapper objectMapper = new ObjectMapper();
        return encodeMessageBytes(objectMapper.writeValueAsBytes(message));
    }

    @SneakyThrows
    public static byte[] encode(Response message){
        ObjectMapper objectMapper = new ObjectMapper();
        return encodeMessageBytes(objectMapper.writeValueAsBytes(message));

    }

    @SneakyThrows
    private static byte[] encodeMessageBytes(byte[] messageBytes) {
        final int messageSize = messageBytes.length + 4 + 4;
        final int bufferSize = 1 + 1 + 8 + 4 + 2 + 2 + messageSize;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize).order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)0x13)
                .put((byte)0x1)
                .putLong(1)
                .putInt(messageSize)
                .putShort(Crc16.calc(buffer.array(), 0, 14))
                .putInt(3)
                .putInt(4)
                .put(messageBytes)
                .putShort(Crc16.calc(buffer.array(), 16, messageSize));
        return buffer.array();
    }
}
