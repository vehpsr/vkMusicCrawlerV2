package com.gans.vk.dashboard.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.gans.vk.service.impl.RatingServiceImpl;

@Controller
public class StatisticsController {

    private static final Log LOG = LogFactory.getLog(StatisticsController.class);

    @Autowired private SessionManager _sessionManager;
    @Autowired private RatingService _ratingService;
    @Autowired private SongService _songService;
    @Autowired private UserService _userService;

    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public String statsPage(HttpServletRequest req, HttpServletResponse resp, Model model) {
        long start = System.currentTimeMillis();

        User user = _sessionManager.getCurrentUser();
        List<Entry<String, Integer>> systemSongStats = _songService.statisticsSongData();
        List<Entry<String, Integer>> systemRatingStats = _ratingService.statisticsRatingData(user);
        List<Entry<String, Integer>> systemUserStats = _userService.statisticsUserData();

        LOG.info(MessageFormat.format("Fetch statistics take: {0}", System.currentTimeMillis() - start));

        List<Entry<String, Integer>> systemStats = new ArrayList<>();
        systemStats.addAll(systemSongStats);
        systemStats.addAll(systemRatingStats);
        systemStats.addAll(systemUserStats);
        model.addAttribute("systemStats", systemStats);

        resp.setContentType("text/html;charset=UTF-8");
        return "stats";
    }

    @RequestMapping(value = "/stats/rating")
    @ResponseBody
    public Entry<Map<Long, Float>, List<RatingData>> rating() {
        User user = _sessionManager.getCurrentUser();
        return _ratingService.songRating(user);
    }

    @RequestMapping(value = "/stats/test")
    @ResponseBody
    public StatNode test() {
        StatNode root = new StatNode("System Stats");
        StatNode testSN = ((RatingServiceImpl)_ratingService).testSN();
        root.addNode(testSN);
        return root;
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
