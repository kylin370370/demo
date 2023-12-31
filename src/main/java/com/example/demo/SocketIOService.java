package com.example.demo;

import com.corundumstudio.socketio.SocketIOServer;
import com.example.demo.util.MongoDB;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.util.*;

@Service
public class SocketIOService {
    private static Map<String, UUID> map=new HashMap<>();
    private final SocketIOServer server;

    @Autowired
    public SocketIOService(SocketIOServer server) {
        this.server = server;
    }

    @PostConstruct
    public void startServer() {

    server.addConnectListener(client -> {
        System.out.println("Client connected: " + client.getSessionId());
    });

    server.addDisconnectListener(client -> {
        System.out.println("Client disconnected: " + client.getSessionId());
    });
    server.addEventListener("Authorize", String.class, (client, data, ackRequest)-> {
                String userid=data;
                if(!map.containsKey(userid)){
                    map.put(userid, client.getSessionId());
                }
    });
    server.addEventListener("updateReadStatus", MessageObject.class, (client, data, ackRequest)-> {
        String userid=data.getReceiverId();
        if(!map.containsKey(userid)){
            map.put(userid, client.getSessionId());
        }
        String sender = data.getSenderId();
        String receiver = data.getReceiverId();
        MongoCollection<Document> collection = MongoDB.getDatabase().getCollection("chat");
        Bson filter = Filters.and(
                Filters.eq("sender", sender),
                Filters.eq("receiver", receiver),
                Filters.eq("isRead", false)
        );

// Create an update operation to set isRead to true
        Bson updateOperation = Updates.set("isRead", true);

// Update all matching documents in the collection
        collection.updateMany(filter, updateOperation);
        server.getClient(map.get(userid)).sendEvent("readStatusUpdated", sender);
//        server.getBroadcastOperations().sendEvent("readStatusUpdated", sender);

    });
    server.addEventListener("requestRecentChats", String.class, (client, data, ackRequest) -> {
        System.out.println("Received userId message: " + data);
        String userId=data;

        MongoCollection<Document> collection = MongoDB.getDatabase().getCollection("chat");
        Set<String> contactIds = new HashSet<>();

                // 查询 sender 或 receiver 为 userId 的记录
                collection.find(
                        Filters.or(
                                Filters.eq("sender", userId),
                                Filters.eq("receiver", userId)
                        )
                ).forEach(doc -> {
                    String sender = doc.getString("sender");
                    String receiver = doc.getString("receiver");
                    if (!sender.equals(userId)) {
                        contactIds.add(sender);
                    }
                    if (!receiver.equals(userId)) {
                        contactIds.add(receiver);
                    }
                });

                List<Document> contacts = new ArrayList<>();
                for (String contactId : contactIds) {
                    contacts.add(new Document("contactId", contactId));
                    System.out.println(contactId);
                }

                client.sendEvent("recentChatResponse", contacts);


    });


    server.addEventListener("fetchChatHistory", MessageObject.class, (client, data, ackRequest) -> {
        String sender = data.getSenderId();
        String receiver = data.getReceiverId();
            // 从 MongoDB 中获取聊天记录
               MongoCollection<Document> collection = MongoDB.getDatabase().getCollection("chat");
//                List<Document> chatHistory = collection.find(
//                        Filters.or(Filters.eq("sender", sender), Filters.eq("sender", receiver))
//                ).sort(Sorts.ascending("timestamp")).into(new ArrayList<>());
                Bson condition = Filters.or(
                        Filters.and(Filters.eq("sender", sender), Filters.eq("receiver", receiver)),
                        Filters.and(Filters.eq("sender", receiver), Filters.eq("receiver", sender))
                );

                List<Document> chatHistory = collection.find(condition)
                        .sort(Sorts.ascending("timestamp"))
                        .into(new ArrayList<>());
//                // 发送聊天记录回客户端
//                client.sendEvent("chatHistoryResponse", chatHistory);
            // 发送聊天记录回客户端
            client.sendEvent("chatHistoryResponse", chatHistory);

    });

    server.addEventListener("messageEvent", MessageObject.class, (client, data, ackRequest) -> {
        System.out.println("Received message: " + data);

            MongoCollection<Document> collection = MongoDB.getDatabase().getCollection("chat");
            System.out.println("Connected successfully");
            // 将 MessageObject 转换为 Document 并插入到集合中
            Document doc = data.toDocument();
            collection.insertOne(doc);
            System.out.println("Document inserted successfully");
            server.getBroadcastOperations().sendEvent("messageEvent", data);
    });
//    server.addEventListener("simpleMessageEvent", String.class, (client, data, ackRequest) -> {
//        System.out.println("Received simple message: " + data);
//        // You can add further processing logic here
//    });
    server.start();
}
    // 添加其他必要的方法...
}


