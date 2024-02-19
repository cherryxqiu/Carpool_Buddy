package com.example.carpoolbuddy.models;

public class Message {
    String messageId;
    String content;
    String preview;
    String sender;

    public Message() {
    }


    public Message(String m, String c, String p, String s){
        this.messageId = m;
        this.content = c;
        this.preview = p;
        this.sender = s;
    }

    public String getContent() {
        return content;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getPreview() {
        return preview;
    }

    public String getSender() {
        return sender;
    }
}
