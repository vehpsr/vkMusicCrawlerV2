package com.gans.vk.dashboard.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gans.vk.service.AudioDiscoveryService;

@Controller
public class DiscoveryController {

    @Autowired
    private AudioDiscoveryService _audioDiscovery;

    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public String discoveryPage(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
        _audioDiscovery.discoverAudioByUserUrl("");
        model.addAttribute("messages", new ArrayList<String>());
        resp.setContentType("text/html;charset=UTF-8");
        return "discover";
    }

    public void setAudioDiscovery(AudioDiscoveryService audioDiscovery) {
        _audioDiscovery = audioDiscovery;
    }

}
