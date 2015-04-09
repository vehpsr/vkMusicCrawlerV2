package com.gans.vk.service;

import java.util.List;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface SongService {

    List<Song> getAllUnratedSongs(User user, int limit);
    Song get(long id);

}
