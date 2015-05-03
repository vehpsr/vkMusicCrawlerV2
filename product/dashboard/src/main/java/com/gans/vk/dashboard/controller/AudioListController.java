package com.gans.vk.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.controller.model.ResponseStatus;
import com.gans.vk.dashboard.session.SessionManager;
import com.gans.vk.dashboard.util.RequestUtils;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.AudioDiscoveryService.AudioData;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.SongService;
import com.gans.vk.service.UserService;

@Controller
public class AudioListController {

    private static final Log LOG = LogFactory.getLog(AudioListController.class);
    private static final int MAX_SONGS_ON_PAGE = 150;

    @Autowired private SongService _songService;
    @Autowired private UserService _userService;
    @Autowired private RatingService _ratingService;
    @Autowired private AudioDiscoveryService _audioDiscovery;
    @Autowired private SessionManager _sessionManager;

    @RequestMapping(value = "/audio/{vkId}", method = RequestMethod.GET)
    public String songs(HttpServletRequest req, HttpServletResponse resp, @PathVariable String vkId, ModelMap model) {
        User user = _sessionManager.getCurrentUser();
        User target = _userService.getByVkId(vkId);
        if (target == null) {
            // TODO error page
            LOG.warn("Fail to find user with vkId: " + vkId);
            model.addAttribute("songs", new ArrayList<String>());
            return "audioList";
        }
        resp.setContentType("text/html;charset=UTF-8");

        List<AudioData> songs = _audioDiscovery.getAllUnratedSongs(target, user, MAX_SONGS_ON_PAGE);
        model.addAttribute("user", target);
        model.addAttribute("songs", songs);
        return "audioList";
    }

    @RequestMapping(value = "/song/rate/{id}", method=RequestMethod.POST)
    @ResponseBody
    public ResponseStatus rateSong(HttpServletRequest req, HttpServletResponse resp, @PathVariable long id) {
        JSONObject json = RequestUtils.getJson(req);
        int rating = ((Number) json.get("value")).intValue();
        User user = _sessionManager.getCurrentUser();
        Song song = _songService.get(id);
        _ratingService.rate(user, song, rating);
        return ResponseStatus.OK;
    }

    @RequestMapping(value = "/user/resolve/{id}", method=RequestMethod.GET)
    public String resolveUser(@PathVariable long id) {
        _userService.resolve(id);
        return "redirect:/";
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }

    public void setUserService(UserService userService) {
        _userService = userService;
    }

    public void setSessionManager(SessionManager sessionManager) {
        _sessionManager = sessionManager;
    }

    public void setAudioDiscovery(AudioDiscoveryService audioDiscovery) {
        _audioDiscovery = audioDiscovery;
    }

}
