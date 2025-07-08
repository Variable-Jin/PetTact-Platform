package com.pettact.api.mongo;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class MongoLogRepository {

    private final MongoClient mongoClient;

    private MongoCollection<Document> collection;

    @Value("${spring.data.mongodb.database}") // application.properties에서 logdb 가져오기
    private String dbName;

    @PostConstruct
    public void init() {
        MongoDatabase db = mongoClient.getDatabase(dbName); // db명: logdb
        this.collection = db.getCollection("httplogs");      // 컬렉션명: httplogs
    }

    public void save(Document doc) {
        collection.insertOne(doc);
    }
}
