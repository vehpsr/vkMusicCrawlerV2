package com.gans.vk.dashboard.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.session.SessionManager;
import com.gans.vk.json.StatNode;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.RatingService.RatingData;
import com.gans.vk.service.SongService;
import com.gans.vk.service.UserService;

@Controller
public class StatisticsController {

    @SuppressWarnings("unused")
    private static final Log LOG = LogFactory.getLog(StatisticsController.class);

    @Autowired private SessionManager _sessionManager;
    @Autowired private RatingService _ratingService;
    @Autowired private SongService _songService;
    @Autowired private UserService _userService;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String statsPage(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/html;charset=UTF-8");
        return "stats";
    }

    @RequestMapping(value = "/stats/system")
    @ResponseBody
    public List<StatNode> systemRating() {
        User user = _sessionManager.getCurrentUser();
        StatNode systemRatingStats = _ratingService.statisticsRatingData(user);
        StatNode systemSongStats = _songService.statisticsSongData();
        StatNode systemUserStats = _userService.statisticsUserData();

        StatNode root = new StatNode("Stats");
        root.addNode(systemSongStats);
        root.addNode(systemRatingStats);
        root.addNode(systemUserStats);

        return Arrays.asList(root);
    }

    @RequestMapping(value = "/stats/rating")
    @ResponseBody
    public Entry<Map<Long, Float>, List<RatingData>> rating() {
        User user = _sessionManager.getCurrentUser();
        return _ratingService.songRating(user);
    }

    public void setSessionManager(SessionManager sessionManager) {
        _sessionManager = sessionManager;
    }

    public void setRatingService(RatingService ratingService) {
        _ratingService = ratingService;
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }

    public void setUserService(UserService userService) {
        _userService = userService;
    }

}
