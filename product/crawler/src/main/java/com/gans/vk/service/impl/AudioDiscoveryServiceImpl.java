package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.SongService;
import com.gans.vk.service.UserService;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.RestUtils;
import com.gans.vk.utils.TextUtils;

// TODO refactor
public class AudioDiscoveryServiceImpl implements AudioDiscoveryService {

    private static final Log LOG = LogFactory.getLog(AudioDiscoveryServiceImpl.class);

    @Autowired private SongService _songService;
    @Autowired private UserService _userService;
    @Autowired private RatingService _ratingService;
    @Autowired private HttpVkConnector _vkConnector;
    private String _vkAudioUrl;
    private String _vkAudioEntityPattern;
    private String _minVkAudioLibSize;
    private String _vkDomain;

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

    @Override
    public List<String> discoverAudioByUserUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return Collections.singletonList("Audiolibrary discovery fail: provided URL was empty.");
        }
        List<String> messages = new ArrayList<>();
        User user = _userService.getByUrl(url);
        if (user == null) {
            messages.add(MessageFormat.format("User with url: {0} does not exists yet", url));
            user = getUserByUrl(url);
        }
        // if user is invalid by vkId ?

        //discover audio data and rate

        return messages;
    }

    private User getUserByUrl(String url) {
        final String AUDIO_COMPONENT_ID = "profile_audios";
        final String ID_LINK_SELECTOR = "a.module_header";
        final String AUDIO_COUNT_COMPONENT_CLASS = "p_header_bottom";

        String vkUserPage = _vkDomain.endsWith("/") ? _vkDomain + url : _vkDomain + "/" + url;
        String html = _vkConnector.get(vkUserPage);
        html = HtmlUtils.sanitizeHtml(html);

        LOG.trace(MessageFormat.format("VK response:\n{0}", html));

        Document doc = Jsoup.parse(html);
        ddosCheck(doc);

        Element audios = doc.getElementById(AUDIO_COMPONENT_ID);
        if (audios == null) {
            LOG.info("Page is closed");
            return null; // TODO closed page user
        }

        for (Element element : audios.getElementsByClass(AUDIO_COUNT_COMPONENT_CLASS)) {// TODO remove loop
            String audioSize = element.text();
            int size = extractNumericValue(audioSize);
            if (size < Integer.valueOf(_minVkAudioLibSize)) {
                return null; // TODO not enough audio user
            }
        }

        String href = audios.select(ID_LINK_SELECTOR).attr("href");
        if (StringUtils.isEmpty(href)) {
            return null; // TODO parser error user
        }

        String id = href.replaceAll("\\D", "");
        if (StringUtils.isEmpty(id)) {
            return null; // TODO parser error user
        }

        String name = doc.getElementsByTag("title").text();
        if (StringUtils.isEmpty(name)) {
            return null;//TODO parser error user
        }

        User user = new User();
        user.setName(name);
        user.setUrl(url);
        user.setVkId(id);
        _userService.save(user);

        return user;
    }

    private int extractNumericValue(String text) {
        String count = text.replaceAll("\\D", "");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void ddosCheck(Document doc) {
        final String ERROR_BACK_BTN_ID = "msg_back_button";
        final String ERROR_MSG_CONTAINER_CLASS = "body";

        Element returnBtn = doc.getElementById(ERROR_BACK_BTN_ID);
        Elements errorMsgContainer = doc.getElementsByClass(ERROR_MSG_CONTAINER_CLASS);
        if (returnBtn != null && !errorMsgContainer.isEmpty()) {
            LOG.warn(MessageFormat.format("Request was blocked. Reason: {0}\n{1}", doc.title(), errorMsgContainer.get(0).text()));
            RestUtils.sleep("2x");
        }
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

    public void setUserService(UserService userService) {
        _userService = userService;
    }

    public void setRatingService(RatingService ratingService) {
        _ratingService = ratingService;
    }

    public void setMinVkAudioLibSize(String minVkAudioLibSize) {
        _minVkAudioLibSize = minVkAudioLibSize;
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain;
    }

}
