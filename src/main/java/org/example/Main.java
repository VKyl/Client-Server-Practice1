package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Main {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static void main(String[] args) {
        Product message = new Product("some product", 25);
        byte[] encoded = encode(message);
        System.out.println(bytesToHex(encoded));
        Product decoded = decode(encoded);
        System.out.println(decoded);
    }

    @SneakyThrows
    public static byte[] encode(Product message){
        byte[] bytes = objectMapper.writeValueAsBytes(message);
        final int messageSize = bytes.length + 4 + 4;
        final int bufferSize = 1 + 1 + 8 + 4 + 2 + 2 + messageSize;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize).order(ByteOrder.BIG_ENDIAN);
        buffer.put((byte)0x13)
                .put((byte)0x1)
                .putLong(1)
                .putInt(messageSize)
                .putShort(Crc16.calc(buffer.array(), 0, 14))
                .putInt(3)
                .putInt(4)
                .put(bytes)
                .putShort(Crc16.calc(buffer.array(), 16, messageSize));
        return buffer.array();
    }

    @SneakyThrows
    public static Product decode(byte[] bytes){
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
        return objectMapper.readValue(messageBytes, Product.class);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}