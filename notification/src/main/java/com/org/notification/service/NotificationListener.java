package com.org.notification.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    @KafkaListener(topics = "emprunt-created", groupId = "notification-group")
    public void handleNotification(String message) {
        System.out.println("=================================================");
        System.out.println(">> NOTIFICATION SERVICE - MESSAGE REÇU DE KAFKA :");
        System.out.println(message);
        System.out.println(">> Email de confirmation envoyé (Simulation).");
        System.out.println("=================================================");
    }
}