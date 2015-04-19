package com.gans.vk.dashboard.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.util.RequestUtils;
import com.gans.vk.service.AudioDiscoveryService;

@Controller
public class DiscoveryController {

    @Autowired
    private AudioDiscoveryService _audioDiscovery;

    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public String discoveryPage(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
        model.addAttribute("messages", new ArrayList<String>());
        resp.setContentType("text/html;charset=UTF-8");
        return "discoveryPage";
    }

    @RequestMapping(value = "/discover/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserByVkUrl(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
        String vkUrl = RequestUtils.getJsonProperty(req, "url", String.class);
        //boolean forceUpdate = RequestUtils.getJsonProperty(req, "forceUpdate", Boolean.class);
        _audioDiscovery.discoverAudioByUserUrl(vkUrl);
        return "";
    }

    public void setAudioDiscovery(AudioDiscoveryService audioDiscovery) {
        _audioDiscovery = audioDiscovery;
    }

}
