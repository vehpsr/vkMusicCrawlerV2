package com.gans.vk.processors;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.User;
import com.gans.vk.model.impl.User.UserStatus;
import com.gans.vk.utils.HtmlUtils;
import com.gans.vk.utils.RestUtils;

public class VkUserPageResponseProcessor {

    private static final Log LOG = LogFactory.getLog(VkUserPageResponseProcessor.class);
    private static final String AUDIO_COMPONENT_ID = "profile_audios";
    private static final String ID_LINK_SELECTOR = "a.module_header";
    private static final String AUDIO_COUNT_COMPONENT_CLASS = "p_header_bottom";

    @Autowired
    private HttpVkConnector _vkConnector;
    private String _vkDomain;
    private int _minAudioLibSize;

    public static boolean hasInvalidUserStatus(User user) {
        if (user == null || StringUtils.isEmpty(user.getVkId())) {
            return true;
        }
        for (UserStatus status : UserStatus.values()) {
            if (user.getVkId().equals(status.name())) {
                return true;
            }
        }
        return false;
    }

    public Entry<String, String> getUserByUrl(String url) {
        Document page = getHtmlPage(url);
        if (page == null) {
            return entry(UserStatus.DELETED.name(), UserStatus.DELETED);
        }

        String name = page.getElementsByTag("title").text();
        if (StringUtils.isEmpty(name)) {
            return entry(UserStatus.PARSER_ERROR.name(), UserStatus.PARSER_ERROR);
        }

        Element audios = page.getElementById(AUDIO_COMPONENT_ID);
        if (audios == null) {
            return entry(name, UserStatus.CLOSED_PAGE);
        }

        int audioCount = extractNumericValue(audios.getElementsByClass(AUDIO_COUNT_COMPONENT_CLASS).text());
        if (audioCount < _minAudioLibSize) {
            return entry(name, UserStatus.NOT_ENOUGH_AUDIO);
        } else if (audioCount > 10 * 1000) { // fuck hoarders
            return entry(name, UserStatus.TO_MANY_AUDIO);
        }

        String idLink = audios.select(ID_LINK_SELECTOR).attr("href");
        if (StringUtils.isEmpty(idLink)) {
            return entry(name, UserStatus.PARSER_ERROR);
        }

        String id = idLink.replaceAll("\\D", "");
        if (StringUtils.isEmpty(id) || id.length() > User.VK_ID_MAX_LEN) {
            return entry(name, UserStatus.PARSER_ERROR);
        }

        return entry(name, id);
    }

    private Entry<String, String> entry(String name, UserStatus userStatus) {
        return entry(name, userStatus.name());
    }

    private Entry<String, String> entry(String name, String id) {
        return new AbstractMap.SimpleEntry<String, String>(StringUtils.left(name, User.NAME_MAX_LEN), id);
    }

    private Document getHtmlPage(String url) {
        int retryCount = 1;
        while (retryCount > 0) {
            String vkUserPage = _vkDomain.endsWith("/") ? _vkDomain + url : _vkDomain + "/" + url;
            String html = _vkConnector.get(vkUserPage);
            html = HtmlUtils.sanitizeHtml(html);
            LOG.trace(MessageFormat.format("VK response:\n{0}", html));

            Document page = Jsoup.parse(html);
            if (isDdosBlocked(page)) {
                RestUtils.sleep("4x");
            } else {
                return page;
            }
            retryCount--;
        }
        return null;
    }

    private boolean isDdosBlocked(Document page) {
        final String ERROR_BACK_BTN_ID = "msg_back_button";
        final String ERROR_MSG_CONTAINER_CLASS = "body";

        Element returnBtn = page.getElementById(ERROR_BACK_BTN_ID);
        Elements errorMsgContainer = page.getElementsByClass(ERROR_MSG_CONTAINER_CLASS);

        boolean blocked = returnBtn != null && !errorMsgContainer.isEmpty();
        if (blocked) {
            LOG.warn(MessageFormat.format("Request was blocked. Reason: {0}\n{1}", page.title(), errorMsgContainer.get(0).text()));
        }
        return blocked;
    }

    private int extractNumericValue(String text) {
        try {
            return Integer.parseInt(text.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setMinVkAudioLibSize(String minVkAudioLibSize) {
        _minAudioLibSize = Integer.valueOf(minVkAudioLibSize);
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain;
    }

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

}
