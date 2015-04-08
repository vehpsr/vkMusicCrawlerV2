package com.gans.vk.dashboard.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gans.vk.dao.SongDao;
import com.gans.vk.model.impl.Song;

@Controller
public class AudioListController {

    @Autowired
    private SongDao _songDao;

    public void setSongDao(SongDao songDao) {
        _songDao = songDao;
    }

    @RequestMapping(value = "/audio", method = RequestMethod.GET)
    public String songs(ModelMap model) {
        List<Song> songs = _songDao.getAll();

        model.addAttribute("songs", songs);
        model.addAttribute("message", "Hello Spring MVC Framework!");
        return "audioList";
    }

    @RequestMapping(value = "/song/rate/{id}", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String rateSong(HttpServletRequest req, HttpServletResponse resp, @PathVariable long id) {
        System.out.println(id);
        try (ServletInputStream is = req.getInputStream()) {
            String body = IOUtils.toString(is);
            System.out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
}