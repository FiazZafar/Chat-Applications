package com.fyp.mychat.model;

public class FCMDataMessage {
    public Message message;

    public FCMDataMessage(String token, String title, String body, String imageUrl, String type,String senderId) {
        this.message = new Message(token, new Data(title, body, imageUrl, type,senderId));
    }

    public static class Message {
        public String token;
        public Data data;

        public Message(String token, Data data) {
            this.token = token;
            this.data = data;
        }
    }

    public static class Data {
        public String title;
        public String body;
        public String url;
        public String type;
        public String senderId;

        public Data(String title, String body, String url, String type,String senderId) {
            this.title = title;
            this.body = body;
            this.url = url;
            this.type = type;
            this.senderId = senderId;
        }
    }
}


