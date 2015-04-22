package com.gans.vk.dashboard.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.util.RequestUtils;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.GroupDiscoveryService;
import com.gans.vk.service.UserService;

@Controller
public class DiscoveryController {

    @Autowired private AudioDiscoveryService _audioDiscovery;
    @Autowired private GroupDiscoveryService _groupDiscovery;
    @Autowired private UserService _userService;

    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public String discoveryPage(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
        model.addAttribute("undiscoveredUsers", _userService.getUndiscoveredUsersCount());
        resp.setContentType("text/html;charset=UTF-8");
        return "discoveryPage";
    }

    @RequestMapping(value = "/discover/user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getUserByVkUrl(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject json = RequestUtils.getJson(req);
        String vkUrl = (String) json.get("url");
        Boolean forceUpdate = (Boolean) json.get("forceUpdate");
        _audioDiscovery.discoverAudioByUserUrl(vkUrl, forceUpdate);
        return "";
    }

    @RequestMapping(value = "/discover/group", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getGroupByVkUrl(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject json = RequestUtils.getJson(req);
        String vkUrl = (String) json.get("url");
        Boolean forceUpdate = (Boolean) json.get("forceUpdate");
        _groupDiscovery.discoverGroupByUserUrl(vkUrl, forceUpdate);
        return "";
    }

    @RequestMapping(value = "/discover/newusers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String discoverNewUsers(HttpServletRequest req, HttpServletResponse resp) {
        JSONObject json = RequestUtils.getJson(req);
        int count = Integer.valueOf((String) json.get("count"));
        return "";
    }

    public void setAudioDiscovery(AudioDiscoveryService audioDiscovery) {
        _audioDiscovery = audioDiscovery;
    }

    public void setGroupDiscovery(GroupDiscoveryService groupDiscovery) {
        _groupDiscovery = groupDiscovery;
    }

    public void setUserService(UserService userService) {
        _userService = userService;
    }

}
