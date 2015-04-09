package com.gans.vk.dashboard.controller;

import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
import com.gans.vk.service.RatingService;
import com.gans.vk.service.SongService;

@Controller
public class AudioListController {

    private static final int MAX_SONGS_ON_PAGE = 100;

    @Autowired
    private SongService _songService;

    @Autowired
    private RatingService _ratingService;

    @Autowired
    private SessionManager _sessionManager;

    @RequestMapping(value = "/audio", method = RequestMethod.GET)
    public String songs(ModelMap model) {
        User user = _sessionManager.getCurrentUser();
        List<Song> songs = _songService.getAllUnratedSongs(user, MAX_SONGS_ON_PAGE);
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

    public void setSessionManager(SessionManager sessionManager) {
        _sessionManager = sessionManager;
    }

}
