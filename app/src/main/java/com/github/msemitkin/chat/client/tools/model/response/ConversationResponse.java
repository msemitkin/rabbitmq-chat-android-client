package com.github.msemitkin.chat.client.tools.model.response;

@SuppressWarnings("unused")
public class ConversationResponse {
    private String conversationName;

    public ConversationResponse(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }
}
