package com.songzheng.timemanager.utils;

import java.util.UUID;

/**
 * Created by make on 2017/4/29.
 */

public class UUIDUtils {
    public static String getId() {
        return UUID.randomUUID().toString();
    }
}
