package com.github.msemitkin.chat.client.utils;

public class Constants {

    public static final int STATUS_OK = 200;
    public static final String RESULT_RECEIVER = "receiver";
    public static final String DATA_RECEIVER = "receiver_data";
    public static final String USERNAME = "username";

    //REST
    public static String HOST = "10.0.2.2";
    public static String PORT = "8080";
    public static String BASE_URL = "http://" + HOST + ":" + PORT;

    //AMQP
    public static String AMQP_HOST = "10.0.2.2";
    public static String AMQP_USER = "guest";
    public static String AMQP_PASSWORE = "guest";
    public static String EXCHANGE_FOR_SENDING = "converstaion.outgoing";
}
