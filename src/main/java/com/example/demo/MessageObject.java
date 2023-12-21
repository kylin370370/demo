package com.example.demo;

import org.bson.Document;

public class MessageObject {
        private String senderId;
        private String receiverId;
        private String message;
        private String timestamp; // 新增时间戳字段
        private boolean isRead; // 新增 isRead 字段


        // sender 的 getter 和 setter
        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        // receiver 的 getter 和 setter
        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        // message 的 getter 和 setter
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        // timestamp 的 getter 和 setter
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
// Existing getters and setters

    // isRead 的 getter 和 setter
    public boolean getIsRead() {return isRead;}

    public void setIsRead(boolean isRead) {this.isRead = isRead;}

    @Override
    public String toString() {
        return "MessageObject{" +
                "sender='" + senderId + '\'' +
                ", receiver='" + receiverId + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", isRead=" + isRead +
                '}';
    }

    public Document toDocument() {
        return new Document("sender", senderId)
                .append("receiver", receiverId)
                .append("message", message)
                .append("timestamp", timestamp)
                .append("isRead", isRead); // 将 isRead 也添加到 Document
    }


}
