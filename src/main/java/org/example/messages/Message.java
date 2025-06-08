package org.example.messages;

public record Message (String senderId, MessagesType messagesType, String productId) {}