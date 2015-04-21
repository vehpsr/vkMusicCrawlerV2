package com.gans.vk.processors;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;

public class VkGroupInfoResponseProcessor {

    @Autowired private HttpVkConnector _vkConnector;

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

}
