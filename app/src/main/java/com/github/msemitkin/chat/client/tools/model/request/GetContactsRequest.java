package com.github.msemitkin.chat.client.tools.model.request;

@SuppressWarnings("unused")
public class GetContactsRequest {
    private final String username;

    public GetContactsRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
