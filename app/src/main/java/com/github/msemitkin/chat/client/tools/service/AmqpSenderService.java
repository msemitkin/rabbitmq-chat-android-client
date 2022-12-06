package com.github.msemitkin.chat.client.tools.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.msemitkin.chat.client.utils.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AmqpSenderService extends IntentService {
    public static String MESSAGE = "message";

    private final ConnectionFactory connectionFactory;

    @SuppressWarnings("unused")
    public AmqpSenderService() {
        this("AmqpSenderService");
    }

    public AmqpSenderService(String name) {
        super(name);
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Constants.AMQP_HOST);
        connectionFactory.setUsername(Constants.AMQP_USER);
        connectionFactory.setPassword(Constants.AMQP_PASSWORE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendMessage(intent.getStringExtra(MESSAGE));
    }

    private void sendMessage(String message) {
        try (Connection connection = this.connectionFactory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.confirmSelect();
            channel.basicPublish(Constants.EXCHANGE_FOR_SENDING, "", null, message.getBytes());
            stopSelf();
        } catch (IOException | TimeoutException e) {
            Log.e(this.getClass().getName(), "Failed to send message", e);
        }
    }
}
