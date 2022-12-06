package com.github.msemitkin.chat.client.tools.model;

@SuppressWarnings("unused")
public class Message {

    private String author;
    private String text;
    private String conversation;
    private String date;

    public Message() {
    }

    public Message(String author, String text, String conversation, String date) {
        this.author = author;
        this.text = text;
        this.conversation = conversation;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
