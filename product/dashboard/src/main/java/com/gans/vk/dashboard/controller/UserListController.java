package com.gans.vk.dashboard.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.dashboard.session.SessionManager;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.UserService;

@Controller
public class UserListController {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(UserListController.class);

    @Autowired private UserService _userService;
    @Autowired private SessionManager _sessionManager;
    @Resource(name="vk.domain") private String _vkDomain;

    @RequestMapping(value = "/", method = RequestMethod.GET) // "/users" route in future
    public String songs(HttpServletRequest req, HttpServletResponse resp, ModelMap model) {
        User currentUser = _sessionManager.getCurrentUser();
        List<UserLibData> users = _userService.getRecomendedAudioLibsFor(currentUser);
        model.addAttribute("users", users);
        model.addAttribute("vkDomainUrl", _vkDomain);
        resp.setContentType("text/html;charset=UTF-8");
        return "userList";
    }

    public void setUserService(UserService userService) {
        _userService = userService;
    }

    public void setSessionManager(SessionManager sessionManager) {
        _sessionManager = sessionManager;
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain.endsWith("/") ? vkDomain : vkDomain + "/";
    }

}
