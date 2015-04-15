package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Song;
import com.gans.vk.model.impl.User;
import com.gans.vk.parser.VkUserAudioResponseParser;
import com.gans.vk.parser.VkUserAudioResponseParser.AudioPart;
import com.gans.vk.parser.VkUserPageResponseParser;
import com.gans.vk.service.AudioDiscoveryService;
import com.gans.vk.service.RatingService;
import com.gans.vk.service.SongService;
import com.gans.vk.service.UserService;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.RestUtils;

// TODO refactor
public class AudioDiscoveryServiceImpl implements AudioDiscoveryService {

    private static final Log LOG = LogFactory.getLog(AudioDiscoveryServiceImpl.class);

    @Autowired private SongService _songService;
    @Autowired private UserService _userService;
    @Autowired private RatingService _ratingService;
    @Autowired private HttpVkConnector _vkConnector;
    @Autowired private VkUserAudioResponseParser _vkAudioParser;
    @Autowired private VkUserPageResponseParser _vkUserPageParser;
    private String _vkAudioUrl;
    private String _vkAudioEntityPattern;
    private int _minAudioLibSize;
    private String _vkDomain;

    @Override
    public List<AudioData> getAllUnratedSongs(User target, User user, int maxSongsOnPage) {
        List<Song> unratedDbSongs = _songService.getAllUnratedSongs(target, user, maxSongsOnPage);
        if (unratedDbSongs.isEmpty()) {
            return Collections.emptyList();
        }
        String response = _vkConnector.post(_vkAudioUrl, MessageFormat.format(_vkAudioEntityPattern, target.getVkId()));
        List<Map<AudioPart, String>> parsedVkSongs = _vkAudioParser.getAudioData(response);

        return merge(unratedDbSongs, parsedVkSongs);
    }

    private List<AudioData> merge(List<Song> unratedDbSongs, List<Map<AudioPart, String>> parsedVkSongs) {
        Map<String, Map<AudioPart, String>> hashToSong = new HashMap<>();
        for(Map<AudioPart, String> vkSong : parsedVkSongs) {
            String hash = hash(vkSong.get(AudioPart.ARTIST), vkSong.get(AudioPart.TITLE));
            hashToSong.put(hash, vkSong);
        }
        List<AudioData> result = new ArrayList<>();
        for (Song song : unratedDbSongs) {
            String hash = hash(song.getArtist(), song.getTitle());
            Map<AudioPart, String> vkSong = hashToSong.get(hash);
            if (vkSong == null) {
                LOG.warn(MessageFormat.format("Fail to fetch from VK song: {0}", song));
                continue;
            }

            AudioData audioData = new AudioData();
            audioData.setId(song.getId());
            audioData.setUrl(vkSong.get(AudioPart.URL));
            audioData.setTime(vkSong.get(AudioPart.TIME));
            audioData.setArtist(vkSong.get(AudioPart.ARTIST));
            audioData.setTitle(vkSong.get(AudioPart.TITLE));
            result.add(audioData);
        }

        return result;
    }

    private static String hash(String artist, String title) {
        return MessageFormat.format("{0} --SEPARATOR-- {1}", artist, title);
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
            if (size < _minAudioLibSize) {
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
        _minAudioLibSize = Integer.valueOf(minVkAudioLibSize);
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain;
    }

    public void setVkAudioParser(VkUserAudioResponseParser vkAudioParser) {
        _vkAudioParser = vkAudioParser;
    }

    public void setVkUserPageParser(VkUserPageResponseParser vkUserPageParser) {
        _vkUserPageParser = vkUserPageParser;
    }

}
