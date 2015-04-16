package com.gans.vk.processors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Song;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.TextUtils;

public class VkUserAudioResponseProcessor {

    private static final Log LOG = LogFactory.getLog(VkUserAudioResponseProcessor.class);
    private static final String ALL_SONGS_PROPERTY = "all";

    @Autowired
    private HttpVkConnector _vkConnector;
    private String _vkAudioUrl;
    private String _vkAudioEntityPattern;

    public enum AudioPart {
        URL(2) {
            @Override
            String transform(String url) {
                return url.split("\\?", 2)[0];
            }
        },
        TIME(4) {
            @Override
            String transform(String time) {
                return time;
            }
        },
        ARTIST(5) {
            @Override
            String transform(String artist) {
                return normalize(artist, Song.ARTIST_MAX_LEN);
            }
        },
        TITLE(6) {
            @Override
            String transform(String title) {
                return normalize(title, Song.TITLE_MAX_LEN);
            }
        };

        private int _pos;

        AudioPart(int pos) {
            _pos = pos;
        }

        abstract String transform(String audioData);

        private static String normalize(String in, int maxLen) {
            if (StringUtils.isEmpty(in)) {
                return "";
            }
            String result = in.toLowerCase();
            result = result.replaceAll("[^-a-zа-я0-9!@#%&=_,<>{}:;' \"\\+\\*\\?\\$\\.\\(\\)\\[\\]]", "?");
            result = result.replaceAll("^(?:(?:[^a-zа-я0-9&#;])|(?:&#?\\d{2,7};))+", "");
            boolean hasData = Pattern.compile("[a-zа-я0-9]").matcher(result).find();
            if (!hasData) { // junk
                return "";
            }
            return StringUtils.left(result, maxLen);
        }
    }

    // TODO check if response is not from me (as result of invalid vkId)
    public List<Map<AudioPart, String>> getAudioData(String vkId) {
        if (StringUtils.isEmpty(vkId)) {
            return Collections.emptyList();
        }
        String response = _vkConnector.post(_vkAudioUrl, MessageFormat.format(_vkAudioEntityPattern, vkId));
        String[] jsonCollection = HtmlUtils.sanitizeJson(response);
        if (jsonCollection.length == 0) {
            LOG.error("Audio library discovery fail");
            LOG.debug(MessageFormat.format("VK response:\n{0}", TextUtils.shortVersion(response)));
            return Collections.emptyList();
        }

        JSONParser parser = new JSONParser();
        List<Map<AudioPart, String>> result = new ArrayList<>();
        try {
            JSONObject json = (JSONObject)parser.parse(jsonCollection[0]);
            JSONArray allSongs = (JSONArray)json.get(ALL_SONGS_PROPERTY);
            @SuppressWarnings("unchecked")
            Iterator<JSONArray> songIterator = allSongs.iterator();
            while (songIterator.hasNext()) {
                JSONArray song = songIterator.next();

                String url = extract(song, AudioPart.URL);
                String time = extract(song, AudioPart.TIME);
                String artist = extract(song, AudioPart.ARTIST);
                String title = extract(song, AudioPart.TITLE);

                if (StringUtils.isEmpty(url) || StringUtils.isEmpty(artist) || StringUtils.isEmpty(title)) {
                    LOG.info(MessageFormat.format("Reject to process song: {0}", song.toJSONString()));
                    continue;
                }

                Map<AudioPart, String> audioData = new HashMap<>();
                audioData.put(AudioPart.URL, url);
                audioData.put(AudioPart.TIME, time);
                audioData.put(AudioPart.ARTIST, artist);
                audioData.put(AudioPart.TITLE, title);
                result.add(audioData);
            }
        } catch (ParseException e) {
            LOG.error(MessageFormat.format("Fail to parse response: {0}\n{1}", e.getMessage(), TextUtils.shortVersion(jsonCollection[0])));
        }

        return result;
    }

    private String extract(JSONArray song, AudioPart part) {
        String audioData = StringUtils.trimToEmpty((String)song.get(part._pos)).trim();
        return part.transform(audioData);
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
