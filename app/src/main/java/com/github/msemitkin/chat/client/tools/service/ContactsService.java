package com.github.msemitkin.chat.client.tools.service;


import static com.github.msemitkin.chat.client.utils.Constants.BASE_URL;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.msemitkin.chat.client.tools.model.request.GetContactsRequest;
import com.github.msemitkin.chat.client.tools.model.response.GetContactsResponse;
import com.github.msemitkin.chat.client.tools.rest.ApiController;
import com.github.msemitkin.chat.client.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactsService extends IntentService {
    private static final String CONTACTS_LIST = "contacts_list";

    private final ApiController apiController;

    @SuppressWarnings("unused")
    public ContactsService() {
        this("ContactsService");
    }

    public ContactsService(String name) {
        super(name);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiController = retrofit.create(ApiController.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String userName = intent.getStringExtra(Constants.USERNAME);
        ResultReceiver receiver = intent.getParcelableExtra(Constants.DATA_RECEIVER);
        final Bundle data = new Bundle();
        ArrayList<String> contactsList = new ArrayList<>();
        try {
            Response<GetContactsResponse> contacts = apiController.getContacts(new GetContactsRequest(userName)).execute();
            contactsList.addAll(contacts.body().getContacts());
        } catch (IOException e) {
            Log.e(this.getClass().getName(), "Failed to fetch contacts", e);
        }
        data.putStringArrayList(CONTACTS_LIST, contactsList);
        receiver.send(Constants.STATUS_OK, data);
    }
}