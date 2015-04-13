package com.gans.vk.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.SongService;

public class AudioDiscoveryServiceImpl implements AudioDiscoveryService {

    @Autowired
    private SongService _songService;

    @Autowired
    private HttpVkConnector _vkConnector;

    @Override
    public List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage) {
        List<Song> unratedSongs = _songService.getAllUnratedSongs(target, user, maxSongsOnPage);
        if (unratedSongs.isEmpty()) {
            return Collections.emptyList();
        }
        return null;
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

}
