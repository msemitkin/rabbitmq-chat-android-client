package com.github.msemitkin.chat.client.utils;

import java.time.Instant;

public class TimeUtils {

    public static String getTime() {
        return String.valueOf(Instant.now().toEpochMilli());
    }
}
