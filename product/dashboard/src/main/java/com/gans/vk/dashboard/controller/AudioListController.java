package com.gans.vk.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String printHello(ModelMap model) {
        List<Song> songs = _songDao.getAll();

        model.addAttribute("songs", songs);
        model.addAttribute("message", "Hello Spring MVC Framework!");
        return "audioList";
    }
}