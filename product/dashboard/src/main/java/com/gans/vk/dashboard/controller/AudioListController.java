package com.gans.vk.dashboard.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dashboard.session.SessionManager;
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
    private static final int MAX_SONGS_ON_PAGE = 100;

    @Autowired
    private SongService _songService;

    @Autowired
    private UserService _userService;

    @Autowired
    private RatingService _ratingService;

    @Autowired
    private AudioDiscoveryService _audioDiscovery;

    @Autowired
    private SessionManager _sessionManager;

    @RequestMapping(value = "/audio/{vkId}", method = RequestMethod.GET)
    public String songs(@PathVariable String vkId, ModelMap model) {
        User user = _sessionManager.getCurrentUser();
        User target = _userService.getByVkId(vkId);
        if (target == null) {
            // TODO error page
            LOG.warn("Fail to find user with vkId: " + vkId);
            model.addAttribute("songs", new ArrayList<String>());
            return "audioList";
        }
        List<AudioData> songs = _audioDiscovery.getAllUnratedSongs(target, user, MAX_SONGS_ON_PAGE);
        model.addAttribute("songs", songs);
        return "audioList";
    }

    @RequestMapping(value = "/song/rate/{id}", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String rateSong(HttpServletRequest req, HttpServletResponse resp, @PathVariable long id) {
        int rating = Integer.valueOf(getJsonProperty(req, "value"));
        User user = _sessionManager.getCurrentUser();
        Song song = _songService.get(id);
        _ratingService.rate(user, song, rating);
        return "ok";
    }

    // TODO things must be much simpler. find alternative solution
    private static String getJsonProperty(ServletRequest req, String propertyName) {
        try (ServletInputStream is = req.getInputStream()) {
            String body = IOUtils.toString(is);
            JSONObject obj = (JSONObject) JSONValue.parse(body);
            return obj.get(propertyName).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
