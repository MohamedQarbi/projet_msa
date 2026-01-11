package com.org.emprunt.service;

import com.org.emprunt.DTO.EmpruntDetailsDTO;
import com.org.emprunt.entities.Emprunter;
import com.org.emprunt.feign.BookClient;
import com.org.emprunt.feign.UserClient;
import com.org.emprunt.repositories.EmpruntRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmpruntService {

    private final EmpruntRepository repo;
    private final UserClient userClient;
    private final BookClient bookClient;
    
    private final KafkaTemplate<String, String> kafkaTemplate;

    public EmpruntService(EmpruntRepository repo, UserClient userClient, BookClient bookClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.repo = repo;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Emprunter createEmprunt(Long userId, Long bookId) {

        // 1. Vérifier user existe 
        userClient.getUser(userId);

        // 2. Vérifier book existe 
        bookClient.getBook(bookId);

        // 3. Créer l’emprunt 
        Emprunter b = new Emprunter();
        b.setUserId(userId);
        b.setBookId(bookId);

        Emprunter savedEmprunt = repo.save(b);

        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            String message = String.format(
                "{\"empruntId\": %d, \"userId\": %d, \"bookId\": %d, \"eventType\": \"EMPRUNT_CREATED\", \"timestamp\": \"%s\"}",
                savedEmprunt.getId(), 
                savedEmprunt.getUserId(), 
                savedEmprunt.getBookId(),
                timestamp
            );

            kafkaTemplate.send("emprunt-created", message);
            
        } catch (Exception e) {
            System.err.println("Échec de l'envoi de la notification Kafka: " + e.getMessage());
        }

        return savedEmprunt;
    }

    public List<EmpruntDetailsDTO> getAllEmprunts() {
        return repo.findAll().stream().map(e -> {

            var user = userClient.getUser(e.getUserId());
            var book = bookClient.getBook(e.getBookId());

            return new EmpruntDetailsDTO(
                    e.getId(),
                    user.getName(),
                    book.getTitle(),
                    e.getEmpruntDate());
        }).collect(Collectors.toList());
    }
}