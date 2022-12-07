package com.github.msemitkin.chat.client.tools.rest;

import com.github.msemitkin.chat.client.tools.model.request.ConversationRequest;
import com.github.msemitkin.chat.client.tools.model.request.LoginRequest;
import com.github.msemitkin.chat.client.tools.model.response.ConversationResponse;
import com.github.msemitkin.chat.client.tools.model.response.GetContactsResponse;
import com.github.msemitkin.chat.client.tools.model.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiController {

    @POST("/api/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("/api/users/{username}/contacts")
    Call<GetContactsResponse> getContacts(@Path("username") String username);

    @GET("/api/conversations")
    Call<ConversationResponse> getConversation(
            @Query("firstUser") String firstUser,
            @Query("secondUser") String secondUser
    );

    @POST("/api/conversations")
    Call<ConversationResponse> createConversation(@Body ConversationRequest createConversationRequest);
}
