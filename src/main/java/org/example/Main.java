package org.example;

public class Main {
    public static void main(String[] args) {
        Product message = new Product("some product", 25);
        byte[] encoded = MessageEncoder.encode(message);
        System.out.println(BytesConverter.bytesToHex(encoded));
        Product decoded = MessageEncoder.decode(encoded);
        System.out.println(decoded);
    }
}