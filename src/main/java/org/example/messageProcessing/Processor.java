package org.example.messageProcessing;

import lombok.AllArgsConstructor;
import org.example.messages.Message;
import org.example.messages.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
public class Processor implements Runnable {
    BlockingQueue<Message> message;
    BlockingQueue<Response> response;
    AtomicBoolean isRunning;

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                response.put(process(message.take()));
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Response process(Message message) {
        switch (message.messagesType()) {
            case REMOVE_PRODUCT -> System.out.println("Removing product " + message.productId());
            case GET_QUANTITY -> System.out.println("Quantity of product " +
                    message.productId() +
                    " is " +
                    (int)(Math.random() * 50)
            );
            default -> throw new RuntimeException("Unknown message type");
        }
        return new Response(200);
    }
}
