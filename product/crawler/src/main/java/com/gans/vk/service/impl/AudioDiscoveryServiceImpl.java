package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.SongDao.SongData;
import com.gans.vk.model.impl.User;
import com.gans.vk.processors.VkUserAudioResponseProcessor;
import com.gans.vk.processors.VkUserAudioResponseProcessor.AudioPart;
import com.gans.vk.processors.VkUserPageResponseProcessor;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.SongService;
import com.gans.vk.service.UserService;

public class AudioDiscoveryServiceImpl implements AudioDiscoveryService {

    private static final Log LOG = LogFactory.getLog(AudioDiscoveryServiceImpl.class);

    @Autowired private SongService _songService;
    @Autowired private UserService _userService;
    @Autowired private RatingService _ratingService;
    @Autowired private VkUserAudioResponseProcessor _vkAudioProcessor;
    @Autowired private VkUserPageResponseProcessor _vkUserPageProcessor;

    @Override
    public List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage) {
        List<SongData> unratedDbSongs = _songService.getAllUnratedSongs(target, user, maxSongsOnPage);
        if (unratedDbSongs.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<AudioPart, String>> audioLib = _vkAudioProcessor.getAudioData(target.getVkId());
        Set<String> excludeArtists = getExcludedArtists(unratedDbSongs);
        return merge(unratedDbSongs, audioLib, excludeArtists, maxSongsOnPage);
    }

    private Set<String> getExcludedArtists(List<SongData> unratedSongs) {
        Set<String> artists = new HashSet<>();
        for (SongData song : unratedSongs) {
            if (song.getArtistRateCount() == 0) {
                continue;
            }
            if (song.getArtistAvgRating() < 3.51f && song.getArtistRateCount() > 3) {
                // 1 - 4; 1.5 - 8; 2 - 12; 2.5 - 16; 3 - 20; 3.5 - 24
                if (song.getArtistRateCount() > 4 + (song.getArtistAvgRating() - 1) * 8) {
                    artists.add(song.getArtist());
                }
            }
        }
        LOG.info("Skip: " + artists);
        return artists;
    }

    private List<AudioData> merge(List<SongData> unratedDbSongs, List<Map<AudioPart, String>> audioLib, Set<String> excludeArtists, int maxSongsOnPage) {
        if (audioLib.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, Map<AudioPart, String>> hashToSong = new HashMap<>();
        for(Map<AudioPart, String> vkSong : audioLib) {
            String hash = hash(vkSong.get(AudioPart.ARTIST), vkSong.get(AudioPart.TITLE));
            hashToSong.put(hash, vkSong);
        }
        List<AudioData> result = new ArrayList<>();
        for (int i = 0; i < unratedDbSongs.size() && result.size() < maxSongsOnPage; i++) {
            SongData song = unratedDbSongs.get(i);
            if (excludeArtists.contains(song.getArtist())) {
                LOG.debug("Skip: " + song.toString());
                continue;
            }

            String hash = hash(song.getArtist(), song.getTitle());
            Map<AudioPart, String> vkSong = hashToSong.get(hash);
            if (vkSong == null) {
                LOG.debug(MessageFormat.format("Fail to fetch from VK song: {0}", song));
                continue;
            }

            AudioData audioData = new AudioData();
            audioData.setUrl(vkSong.get(AudioPart.URL));
            audioData.setTime(vkSong.get(AudioPart.TIME));
            audioData.setArtist(vkSong.get(AudioPart.ARTIST));
            audioData.setTitle(vkSong.get(AudioPart.TITLE));
            audioData.setId(song.getId());
            audioData.setArtistRateCount(song.getArtistRateCount());
            audioData.setArtistAvgRating(song.getArtistAvgRating());
            result.add(audioData);
        }

        return result;
    }

    private static String hash(String artist, String title) {
        return MessageFormat.format("{0} --SEPARATOR-- {1}", artist, title);
    }

    @Override
    public void discoverAudioByUserUrl(String url, boolean forceUpdate) {
        if (StringUtils.isEmpty(url)) {
            LOG.warn("Audiolibrary discovery fail: provided URL was empty.");
            return;
        }

        User user = _userService.getByUrl(url);
        if (!forceUpdate && (user != null && StringUtils.isNotEmpty(user.getVkId()))) {
            LOG.info(MessageFormat.format("User {0} already exists in system", user));
            return;
        }

        if (user == null) {
            user = new User();
            user.setUrl(url);
        }

        syncUserAudioData(user);
    }

    @Override
    public void discoverNewUsers(int limit) {
        List<User> newUsers = _userService.getUndiscoveredUsers(limit);
        for (User user : newUsers) {
            syncUserAudioData(user);
        }
        LOG.info("New user discovery end");
    }

    private void syncUserAudioData(User user) {
        Entry<String, String> userData = _vkUserPageProcessor.getUserByUrl(user.getUrl());
        user.setName(userData.getKey());
        user.setVkId(userData.getValue());
        _userService.save(user);

        if (VkUserPageResponseProcessor.hasInvalidUserStatus(user)) {
            LOG.info(MessageFormat.format("Stop audio discovery for user {0}", user));
            return;
        }

        List<Map<AudioPart, String>> audioLib = _vkAudioProcessor.getAudioData(user.getVkId());
        Set<Entry<String, String>> songData = extractArtistAndTitleData(audioLib);
        _ratingService.importUserAudioLib(user, songData);
    }

    private Set<Entry<String, String>> extractArtistAndTitleData(List<Map<AudioPart, String>> audioLib) {
        Set<Entry<String, String>> result = new HashSet<>();
        for (Map<AudioPart, String> audio : audioLib) {
            result.add(new AbstractMap.SimpleEntry<String, String>(audio.get(AudioPart.ARTIST), audio.get(AudioPart.TITLE)));
        }
        return result;
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }

    public void setUserService(UserService userService) {
        _userService = userService;
    }

    public void setRatingService(RatingService ratingService) {
        _ratingService = ratingService;
    }

    public void setVkAudioProcessor(VkUserAudioResponseProcessor vkAudioProcessor) {
        _vkAudioProcessor = vkAudioProcessor;
    }

    public void setVkUserPageProcessor(VkUserPageResponseProcessor vkUserPageProcessor) {
        _vkUserPageProcessor = vkUserPageProcessor;
    }

}
