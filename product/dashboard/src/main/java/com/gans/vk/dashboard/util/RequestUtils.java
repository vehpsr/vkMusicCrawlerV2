package com.gans.vk.dashboard.util;

import java.text.MessageFormat;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RequestUtils {

    // TODO things must be much simpler. find alternative solution
    @SuppressWarnings("unchecked")
    public static <T> T getJsonProperty(ServletRequest req, String propertyName, Class<T> returnType) {
        try (ServletInputStream is = req.getInputStream()) {
            String body = IOUtils.toString(is);
            JSONObject obj = (JSONObject) JSONValue.parse(body);
            String value = (String) obj.get(propertyName);
            if (value == null) {
                throw new IllegalStateException(MessageFormat.format("Fail to find {0} in incoming JSON request", propertyName));
            }
            if (returnType.equals(String.class)) {
            	return (T) value;
            } else if (returnType.equals(Boolean.class)) {
                return (T) Boolean.valueOf(value);
            } else if (returnType.equals(Integer.class)) {
                return (T) Integer.valueOf(value);
            } else {
                throw new IllegalArgumentException(MessageFormat.format("Unsupported type {0}", returnType));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
