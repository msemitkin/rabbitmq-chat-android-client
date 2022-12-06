package com.github.msemitkin.chat.client.tools.model.response;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class LoginResponse {
    @SerializedName("exchange")
    private String exchange;

    public LoginResponse(String exchange) {
        this.exchange = exchange;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
