package org.example.messageProcessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.example.Crc16;
import org.example.messages.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class Decryptor implements Runnable {
    BlockingQueue<byte[]> request;
    BlockingQueue<Message> message;
    AtomicBoolean isRunning;

    @Override
    public void run() {
        try{
            while (isRunning.get()) {
                message.put(decode(request.take()));
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Message decode(byte[] bytes){
        ObjectMapper objectMapper = new ObjectMapper();
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        byte mByte = buffer.get();
        if (mByte != 0x13)
            throw new IllegalArgumentException();
        byte bSrc = buffer.get();
        long bPktId = buffer.getLong();
        int wLen = buffer.getInt();
        short wCrc16 = buffer.getShort();
        short expectedCrc16 = Crc16.calc(buffer.array(), 0, 14);
        if (wCrc16 != expectedCrc16)
            throw new IllegalArgumentException();
        int cType = buffer.getInt();
        int bUserId = buffer.getInt();
        int messageSize = wLen - 8;
        byte[] messageBytes = new byte[messageSize];
        buffer.get(messageBytes, 0, messageSize);
        short w2Crc16 = buffer.getShort(bytes.length - 2);
        short expectedCrc2 = Crc16.calc(buffer.array(), 16, wLen);
        if (w2Crc16 != expectedCrc2)
            throw new IllegalArgumentException();
        return objectMapper.readValue(messageBytes, Message.class);
    }
}
