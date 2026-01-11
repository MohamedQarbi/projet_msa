# Projet MSA : Application de Gestion d'Emprunts

Ce projet est une application de gestion d'emprunts basée sur une architecture **Microservices**. Il s'agit d'une extension d'une architecture existante pour intégrer la persistance des données, la communication asynchrone et un service de notification découplé.


##  Réalisations et Implémentations

Conformément aux objectifs du projet, nous avons effectué les extensions suivantes :

### 1. Persistance des Données (MySQL)
Mise en place du pattern **Database per Service** pour garantir l'indépendance des données :
* **User Service** est connecté à la base de données `db_user`.
* **Book Service** est connecté à la base de données `db_book`.
* **Emprunter Service** est connecté à la base de données `db_emprunter`.

### 2. Communication Asynchrone (Apache Kafka)
Intégration de Kafka pour découpler la logique métier de la logique de notification :
* **Topic** : `emprunt-created`.
* **Producteur** : Le service `emprunter-service` publie un événement dans ce topic lors de la création d'un emprunt.
* **Format de l'événement** :
    ```json
    {
      "empruntId": 1,
      "userId": 3,
      "bookId": 5,
      "eventType": "EMPRUNT_CREATED",
      "timestamp": "2026-01-10T14:00:00"
    }
    ```

### 3. Service de Notification
Développement et déploiement du **Notification Service** qui :
* Agit uniquement comme un **Consumer Kafka**.
* N'expose aucun endpoint REST entrant (pas d'appel synchrone direct).
* Simule l'envoi de notification (via logs/console) dès la réception d'un événement `EMPRUNT_CREATED`.

### 4. Déploiement
* Configuration complète via **Docker Compose** pour orchestrer l'ensemble des services, les bases de données MySQL, Zookeeper et Kafka.


---
**Réalisé par :**
QARBI MOHAMED DSE