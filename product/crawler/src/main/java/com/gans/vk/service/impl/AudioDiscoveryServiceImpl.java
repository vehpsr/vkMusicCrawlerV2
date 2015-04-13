package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.SongService;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.TextUtils;

public class AudioDiscoveryServiceImpl implements AudioDiscoveryService {

    private static final Log LOG = LogFactory.getLog(AudioDiscoveryServiceImpl.class);

    @Autowired private SongService _songService;
    @Autowired private HttpVkConnector _vkConnector;
    private String _vkAudioUrl;
    private String _vkAudioEntityPattern;

    @Override
    public List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage) {
        List<Song> unratedSongs = _songService.getAllUnratedSongs(target, user, maxSongsOnPage);
        if (unratedSongs.isEmpty()) {
            return Collections.emptyList();
        }
        String response = _vkConnector.post(_vkAudioUrl, MessageFormat.format(_vkAudioEntityPattern, target.getVkId()));
        String[] jsonCollection = HtmlUtils.sanitizeJson(response);
        if (jsonCollection.length == 0) {
            LOG.error(MessageFormat.format("Fail to discover audio lib {0}", target.getVkId()));
            LOG.debug(MessageFormat.format("VK response:\n{0}", TextUtils.shortVersion(response)));
            return Collections.emptyList();
        }
        Map<Integer, String[]> vkMap = parseAudioList(jsonCollection[0]);
        Map<Integer, Long> storageMap = toMap(unratedSongs);

        List<AudioData> result = new ArrayList<>();
        for (Entry<Integer, Long> entry : storageMap.entrySet()) {
            String[] songData = vkMap.get(entry.getKey());
            if (songData == null) {
                LOG.warn(MessageFormat.format("Fail to fetch from VK song: {0}", entry.getValue()));
                continue;
            }
            AudioData audioData = new AudioData();
            audioData.setId(entry.getValue());
            audioData.setUrl(songData[0]);
            audioData.setTime(songData[1]);
            audioData.setArtist(songData[2]);
            audioData.setTitle(songData[3]);
            result.add(audioData);
        }

        return result;
    }

    private Map<Integer, Long> toMap(List<Song> unratedSongs) {
        Map<Integer, Long> result = new HashMap<>();
        for (Song song : unratedSongs) {
            result.put((song.getArtist() + song.getTitle()).hashCode(), song.getId());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String[]> parseAudioList(String json) {
        final String ALL_SONGS_PROPERTY = "all";
        final int URL_POSITION = 2;
        final int TIME_POSITION = 4;
        final int ARTIST_POSITION = 5;
        final int TITLE_POSITION = 6;

        Map<Integer, String[]> result = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject)parser.parse(json);
            JSONArray allSongs = (JSONArray)obj.get(ALL_SONGS_PROPERTY);
            Iterator<JSONArray> songIterator = allSongs.iterator();
            while (songIterator.hasNext()) {
                JSONArray song = songIterator.next();
                String url = ((String)song.get(URL_POSITION)).split("\\?")[0];
                String time = (String)song.get(TIME_POSITION);
                String artist = ((String)song.get(ARTIST_POSITION)).toLowerCase().trim();
                String title = ((String)song.get(TITLE_POSITION)).toLowerCase().trim();
                result.put((artist + title).hashCode(), new String[]{url, time, artist, title});
            }
        } catch (ParseException e) {
            LOG.error(MessageFormat.format("Fail to parse response: {0}\n{1}", e.getMessage(), TextUtils.shortVersion(json)));
        }
        return result;
    }

    public void setSongService(SongService songService) {
        _songService = songService;
    }

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

    public void setVkAudioUrl(String vkAudioUrl) {
        _vkAudioUrl = vkAudioUrl;
    }

    public void setVkAudioEntityPattern(String vkAudioEntityPattern) {
        _vkAudioEntityPattern = vkAudioEntityPattern;
    }

}
