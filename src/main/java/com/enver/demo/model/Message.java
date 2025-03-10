package com.enver.demo.model;

import lombok.*;

@Data
public class Message {
    private String content;

    public Message(String content) {
        this.content = content;
    }

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
