package com.github.msemitkin.chat.client.tools.rest;

import com.github.msemitkin.chat.client.tools.model.request.ConversationRequest;
import com.github.msemitkin.chat.client.tools.model.request.GetContactsRequest;
import com.github.msemitkin.chat.client.tools.model.request.LoginRequest;
import com.github.msemitkin.chat.client.tools.model.response.GetContactsResponse;
import com.github.msemitkin.chat.client.tools.model.response.ConversationResponse;
import com.github.msemitkin.chat.client.tools.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiController {

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/register")
    Call<String> register(@Body LoginRequest loginRequest);

    @POST("/api/contacts")
    Call<GetContactsResponse> getContacts(@Body GetContactsRequest getContactsRequest);

    @POST("/api/conversation")
    Call<ConversationResponse> getConversation(@Body ConversationRequest getConversationRequest);

    @POST("/api/conversation/create")
    Call<ConversationResponse> createConversation(@Body ConversationRequest createConversationRequest);
}
