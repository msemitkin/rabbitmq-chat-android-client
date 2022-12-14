package com.github.msemitkin.chat.client.ui.activities;

import static com.github.msemitkin.chat.client.ui.activities.ConversationActivity.CONVERSATION_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.msemitkin.chat.client.R;
import com.github.msemitkin.chat.client.tools.service.ContactsService;
import com.github.msemitkin.chat.client.tools.service.ConversationService;
import com.github.msemitkin.chat.client.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends BaseNavigationDrawerActivity {
    private static final String USERNAME = "username";
    private static final String EXCHANGE = "exchange";

    private List<String> contacts;
    private ArrayAdapter<String> arrayAdapter;
    private ContactsReceiver contactsReceiver;
    private ConversationReceiver conversationReceiver;
    private String userName;
    private String exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userName = intent.getStringExtra(USERNAME);
        exchange = intent.getStringExtra(EXCHANGE);
        saveUserName(userName);
        inflateWithLayout(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView contactsList = findViewById(R.id.contactsList);
        contacts = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.contacts_item, R.id.contact, contacts);
        contactsList.setAdapter(arrayAdapter);
        contactsList.setOnItemClickListener((adapterView, view, i, l) -> {
            String secondUser = contacts.get(i);
            getConversation(userName, secondUser);
        });
    }

    private void getConversation(String userName, String secondUser) {
        Intent intent = new Intent(this, ConversationService.class);
        intent.putExtra(Constants.DATA_RECEIVER, conversationReceiver);
        intent.putExtra(ConversationService.FIRST_USER, userName);
        intent.putExtra(ConversationService.SECOND_USER, secondUser);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.contactsReceiver = new ContactsReceiver(new Handler());
        this.conversationReceiver = new ConversationReceiver(new Handler());
        getContacts();
    }

    private void getContacts() {
        Intent intent = new Intent(this, ContactsService.class);
        intent.putExtra(Constants.DATA_RECEIVER, contactsReceiver);
        intent.putExtra(Constants.USERNAME, userName);
        startService(intent);
    }

    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, userName);
        editor.commit();
    }

    public void inflateWithLayout(int layoutResID) {
        if (contentActivity != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            View stubView = inflater.inflate(layoutResID, contentActivity, false);
            contentActivity.addView(stubView, lp);
        }
    }

    private class ContactsReceiver extends ResultReceiver {

        private static final String CONTACTS_LIST = "contacts_list";

        public ContactsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            contacts.clear();
            contacts.addAll(resultData.getStringArrayList(CONTACTS_LIST));
            arrayAdapter.notifyDataSetChanged();
        }
    }

    private class ConversationReceiver extends ResultReceiver {


        public ConversationReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String conversationName = resultData.getString(CONVERSATION_NAME);
            Toast toast = Toast.makeText(getBaseContext(), conversationName, Toast.LENGTH_LONG);
            toast.show();
            Intent intent = new Intent(ContactsActivity.this, ConversationActivity.class);
            intent.putExtra(USERNAME, userName);
            intent.putExtra(EXCHANGE, exchange);
            intent.putExtra(CONVERSATION_NAME, conversationName);
            ContactsActivity.this.startActivity(intent);
        }
    }
}
