package com.github.msemitkin.chat.client.tools.model.response;

import java.util.List;

@SuppressWarnings("unused")
public class GetContactsResponse {

    private List<String> contacts;

    public GetContactsResponse(List<String> contacts) {
        this.contacts = contacts;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
}
