package com.github.msemitkin.chat.client.tools.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.msemitkin.chat.client.utils.Constants;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AmqpListenerService extends IntentService {
    public static final String USERNAME = "username";
    public static final String EXCHANGE = "exchange";
    public static final String MESSAGE = "message";

    private ConnectionFactory connectionFactory;
    private String exchange;
    private String queueName;
    ResultReceiver receiver;

    private Thread subscribeThread;

    public AmqpListenerService() {
        this("AmqpListenerService");
    }

    public AmqpListenerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String username = intent.getStringExtra(USERNAME);
        exchange = intent.getStringExtra(EXCHANGE);
        queueName = username + ".messages";
        receiver = intent.getParcelableExtra(Constants.DATA_RECEIVER);
        declareListener();
    }

    private void declareListener() {
        subscribeThread = new Thread(() -> {
            try {
                connectionFactory = new ConnectionFactory();
                connectionFactory.setHost(Constants.AMQP_HOST);
                connectionFactory.setUsername(Constants.AMQP_USER);
                connectionFactory.setPassword(Constants.AMQP_PASSWORE);
                Connection connection = connectionFactory.newConnection();
                Channel channel = connection.createChannel();
                channel.confirmSelect();
                AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, false, false, false, null);
                channel.queueBind(queueName, exchange, "");
                QueueingConsumer consumer = new QueueingConsumer(channel);
                channel.basicConsume(declareOk.getQueue(), true, consumer);
                Log.i(this.getClass().getName(), "Waiting for messages from queue ".concat(queueName)
                        .concat(" on exchange ").concat(exchange));
                while (true) {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String message = new String(delivery.getBody());
                    Bundle bundle = new Bundle();
                    bundle.putString(MESSAGE, message);
                    receiver.send(Constants.STATUS_OK, bundle);
                }
            } catch (IOException | TimeoutException | InterruptedException e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        });
        subscribeThread.start();
    }

    @Override
    public void onDestroy() {
        subscribeThread.interrupt();
        super.onDestroy();
    }
}
