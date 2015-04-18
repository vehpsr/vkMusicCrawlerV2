package com.gans.vk.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;

public interface SongDao extends ModelDao<Song> {

    List<Song> getAllUnratedSongs(User target, User user, int limit);
    Map<String, Entry<Integer, Float>> getArtistData(Collection<String> artists, User user);

}
