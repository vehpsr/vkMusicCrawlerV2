package com.gans.vk.dashboard.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.session.SessionManager;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.RatingService.UserRatingData;

@Controller
public class StatisticsController {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(StatisticsController.class);

    @Autowired private SessionManager _sessionManager;
    @Autowired private RatingService _ratingService;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String statsPage() {
        return "stats";
    }

    @RequestMapping(value = "/stats/user/{userId}")
    @ResponseBody
    public List<UserRatingData> rateSong(@PathVariable long userId) {
        User currentUser = _sessionManager.getCurrentUser();
        return _ratingService.rating(currentUser, null);
    }

    public void setSessionManager(SessionManager sessionManager) {
        _sessionManager = sessionManager;
    }

    public void setRatingService(RatingService ratingService) {
        _ratingService = ratingService;
    }

}
