package com.gans.vk.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.SongService;

public class AudioDiscoveryDummyLocalServiceImpl extends AudioDiscoveryServiceImpl {

    private static final String[] URLS = {
        "http://kolber.github.io/audiojs/demos/mp3/01-dead-wrong-intro.mp3",
        "http://kolber.github.io/audiojs/demos/mp3/02-juicy-r.mp3",
        "http://kolber.github.io/audiojs/demos/mp3/03-its-all-about-the-crystalizabeths.mp3",
        "http://kolber.github.io/audiojs/demos/mp3/04-islands-is-the-limit.mp3",
        "http://kolber.github.io/audiojs/demos/mp3/05-one-more-chance-for-a-heart-to-skip-a-beat.mp3"
    };
    private static final Random RAND = new Random();

    @Autowired private SongService _songService;

    @Override
    public List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage) {
        List<Song> unratedSongs = _songService.getAllUnratedSongs(target, user, maxSongsOnPage);
        if (unratedSongs.isEmpty()) {
            return Collections.emptyList();
        }
        List<AudioData> result = new ArrayList<>();
        for (Song song : unratedSongs) {
            AudioData data = new AudioData();
            data.setArtist(song.getArtist());
            data.setTitle(song.getTitle());
            data.setId(song.getId());
            data.setTime("2:42");
            data.setUrl(randomUrl());
            result.add(data);
        }
        return result;
    }

    private String randomUrl() {
        return URLS[RAND.nextInt(URLS.length)];
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }
}
