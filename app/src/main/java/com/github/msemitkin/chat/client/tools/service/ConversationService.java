package com.github.msemitkin.chat.client.tools.service;


import static com.github.msemitkin.chat.client.ui.activities.ConversationActivity.CONVERSATION_NAME;
import static com.github.msemitkin.chat.client.utils.Constants.BASE_URL;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.msemitkin.chat.client.tools.model.request.ConversationRequest;
import com.github.msemitkin.chat.client.tools.model.response.ConversationResponse;
import com.github.msemitkin.chat.client.tools.rest.ApiController;
import com.github.msemitkin.chat.client.utils.Constants;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConversationService extends IntentService {
    public static final String FIRST_USER = "firstUser";
    public static final String SECOND_USER = "secondUser";

    private final ApiController apiController;

    @SuppressWarnings("unused")
    public ConversationService() {
        this("ContactsService");
    }

    public ConversationService(String name) {
        super(name);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiController = retrofit.create(ApiController.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String firsUser = intent.getStringExtra(FIRST_USER);
        String secondUser = intent.getStringExtra(SECOND_USER);
        ResultReceiver receiver = intent.getParcelableExtra(Constants.DATA_RECEIVER);
        final Bundle data = new Bundle();
        String conversationName = "";
        try {
            Response<ConversationResponse> conversation = apiController.getConversation(new ConversationRequest(firsUser, secondUser)).execute();
            conversationName = conversation.body().getConversationName();
            if (conversationName == null || conversationName.isEmpty()) {
                Response<ConversationResponse> newConversation = apiController.createConversation(new ConversationRequest(firsUser, secondUser)).execute();
                conversationName = newConversation.body().getConversationName();
            }
        } catch (IOException e) {
            Log.e(this.getClass().getName(), "Failed to fetch conversation", e);
        }
        data.putString(CONVERSATION_NAME, conversationName);
        receiver.send(Constants.STATUS_OK, data);
    }
}
