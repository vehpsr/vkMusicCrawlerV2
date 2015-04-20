package com.gans.vk.dashboard.util;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RequestUtils {

    // TODO things must be much simpler. find alternative solution
    public static JSONObject getJson(ServletRequest req) {
        try (ServletInputStream is = req.getInputStream()) {
            String body = IOUtils.toString(is);
            return (JSONObject) JSONValue.parse(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
