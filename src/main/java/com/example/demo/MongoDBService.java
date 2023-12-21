package com.example.demo;

import com.mongodb.client.MongoClient;
        import com.mongodb.client.MongoClients;
        import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MongoDBService {

    @PostConstruct
    public void init() {
        // Connect to MongoDB service
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Connect to the database
            MongoDatabase database = mongoClient.getDatabase("db");
            System.out.println("Connect to database successfully");

            MongoCollection<Document> collection = database.getCollection("chat");

//            // Create a document to insert
//            Document doc = new Document("name", "John Doe")
//                    .append("age", 30)
//                    .append("city", "New York");
//
//            // Insert the document into the collection
//            collection.insertOne(doc);
//            System.out.println("Document inserted successfully");

            // Here you can add more database operation logic
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
    }
}
