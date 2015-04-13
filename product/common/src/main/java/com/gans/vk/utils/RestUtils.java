package com.gans.vk.utils;

import org.apache.commons.lang3.StringUtils;

public class RestUtils {

    public static void sleep() {
        try {
            Thread.sleep(2000);//(long)(SystemProperties.get(CRAWLER_DDOS_TIMEOUT_MIN) + SystemProperties.get(CRAWLER_DDOS_TIMEOUT_RAND) * Math.random()));
        } catch (InterruptedException ignore) {}
    }

    public static void sleep(String cycles) {
        if (StringUtils.isEmpty(cycles)) {
            sleep();
            return;
        }
        String digits = cycles.replaceAll("\\D", "");
        if (StringUtils.isEmpty(digits)) {
            sleep();
            return;
        }
        int retryCount = Integer.parseInt(digits);
        for (int i = 0; i < retryCount; i++) {
            sleep();
        }
    }
}
