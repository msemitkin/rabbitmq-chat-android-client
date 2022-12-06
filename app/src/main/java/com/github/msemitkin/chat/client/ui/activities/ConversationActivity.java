package com.github.msemitkin.chat.client.ui.activities;

import static com.github.msemitkin.chat.client.utils.TimeUtils.getTime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.github.msemitkin.chat.client.R;
import com.github.msemitkin.chat.client.tools.model.Message;
import com.github.msemitkin.chat.client.tools.service.AmqpSenderService;
import com.github.msemitkin.chat.client.ui.adapters.MessageListAdapter;
import com.github.msemitkin.chat.client.utils.Constants;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class ConversationActivity extends AppCompatActivity {

    public static final String USERNAME = "username";
    public static final String EXCHANGE = "exchange";
    public static final String CONVERSATION_NAME = "conversationName";
    public static final String MESSAGE = "message";

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private EditText editText;
    private Button button;
    private List<Message> messageList;
    private Gson gson;
    private MessageReciver messageReciver;
    private String username;
    private String exchange;
    private String conversationName;

    private Thread subscribeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gson = new Gson();

        username = getIntent().getStringExtra(USERNAME);
        exchange = getIntent().getStringExtra(EXCHANGE).replaceAll("\\s+", "");
        conversationName = getIntent().getStringExtra(CONVERSATION_NAME);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        editText = (EditText) findViewById(R.id.edittext_chatbox);
        button = (Button) findViewById(R.id.button_chatbox_send);

        messageList = new ArrayList<>();
        mMessageAdapter = new MessageListAdapter(messageList, username);

        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        button.setOnClickListener(view -> {
            Log.i(this.getClass().getName(), "clicked send button");
            sendMessage(new Message(username, editText.getText().toString(), conversationName, getTime()));
        });
        this.messageReciver = new MessageReciver(new Handler());

        subscribe(new MessageReciver(new Handler()));
    }

    private void sendMessage(Message message) {
        Intent intent = new Intent(this, AmqpSenderService.class);
        intent.putExtra(AmqpSenderService.MESSAGE, gson.toJson(message));
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.messageReciver = new MessageReciver(new Handler());
    }

    @Override
    protected void onDestroy() {
        this.subscribeThread.interrupt();
        super.onDestroy();
    }

    private void subscribe(final ResultReceiver receiver) {
        subscribeThread = new Thread(() -> {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost(Constants.AMQP_HOST);
            connectionFactory.setUsername(Constants.AMQP_USER);
            connectionFactory.setPassword(Constants.AMQP_PASSWORE);

            Channel channel = null;
            QueueingConsumer consumer;
            QueueingConsumer.Delivery delivery;
            try (Connection connection = connectionFactory.newConnection()) {
                while (true) {
                    try {
                        channel = connection.createChannel();
                        channel.confirmSelect();
                        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(username + ".messages", false, false, false, null);
                        channel.queueBind(username + ".messages", exchange, "");
                        consumer = new QueueingConsumer(channel);
                        channel.basicConsume(declareOk.getQueue(), true, consumer);
                        while (true) {
                            delivery = consumer.nextDelivery();
                            String message = new String(delivery.getBody());
                            Bundle bundle = new Bundle();
                            bundle.putString(MESSAGE, message);
                            receiver.send(Constants.STATUS_OK, bundle);
                        }
                    } catch (IOException e) {
                        Log.e(this.getClass().getName(), e.getMessage(), e);
                        break;
                    } catch (InterruptedException e) {
                        try {
                            Log.e(this.getClass().getName(), e.getMessage(), e);
                            channel.abort();
                        } catch (IOException e1) {
                            Log.e(this.getClass().getName(), e.getMessage(), e1);
                        }
                        break;
                    }
                }
            } catch (IOException | TimeoutException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        });
        subscribeThread.start();
    }


    private class MessageReciver extends ResultReceiver {

        public MessageReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            String message = resultData.getString(MESSAGE);
            messageList.add(gson.fromJson(message, Message.class));
            mMessageAdapter.notifyDataSetChanged();
        }
    }
}
