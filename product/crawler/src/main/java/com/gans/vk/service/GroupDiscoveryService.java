package com.gans.vk.service;

public interface GroupDiscoveryService {

    void discoverGroupByUserUrl(String vkUrl, boolean forceUpdate);

}
