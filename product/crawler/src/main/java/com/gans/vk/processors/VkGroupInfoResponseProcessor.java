package com.gans.vk.processors;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.httpclient.HttpVkConnector;
import com.gans.vk.model.impl.Group;
import com.gans.vk.utils.HtmlUtils;

public class VkGroupInfoResponseProcessor {

    private static final Log LOG = LogFactory.getLog(VkGroupInfoResponseProcessor.class);
    private static final String GROUP_ID_CONTAINER = "group_followers";
    private static final String PUBLIC_PAGE_ID_CONTAINER = "public_followers";
    private static final String GROUP_ID_CONTAINER_CLASS = "module_header";

    @Autowired private HttpVkConnector _vkConnector;
    private String _vkDomain;

    enum GroupStatus {
        NOT_FOUND, PARSER_ERROR
    }

    public Entry<String, String> getGroupInfo(String vkUrl) {
        String html = _vkConnector.get(_vkDomain + vkUrl);
        html = HtmlUtils.sanitizeHtml(html);
        if (StringUtils.isEmpty(html)) {
            return entry(GroupStatus.NOT_FOUND.name(), GroupStatus.NOT_FOUND);
        }

        Document doc = Jsoup.parse(html);
        ensureUserLoggedIn(doc);

        String name = doc.getElementsByTag("title").text();
        if (StringUtils.isEmpty(name)) {
            name = GroupStatus.PARSER_ERROR.name();
        }

        Element infoContainer = doc.getElementById(GROUP_ID_CONTAINER);
        if (infoContainer == null) {
            infoContainer = doc.getElementById(PUBLIC_PAGE_ID_CONTAINER);
        }
        if (infoContainer == null) {
            return entry(name, GroupStatus.PARSER_ERROR);
        }

        Element groupIdContainer = infoContainer.getElementsByClass(GROUP_ID_CONTAINER_CLASS).get(0);
        String id = parseGroupId(groupIdContainer.attr("href"));
        if (StringUtils.isEmpty(id)) {
            return entry(name, GroupStatus.PARSER_ERROR);
        }

        return entry(name, id);
    }

    private void ensureUserLoggedIn(Document doc) {
        final String LOGIN_FORM_ID = "quick_login_form";
        if (doc.getElementById(LOGIN_FORM_ID) != null) {
            LOG.error("Parsing GroupInfo fails: user must be login into system to continue");
            throw new AssertionError("Invalid VK credentials");
        }
    }

    private String parseGroupId(String attr) {
        Matcher matcher = Pattern.compile("\\[group\\]=(\\d+)").matcher(attr);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    public static boolean hasInvalidGroupStatus(Group group) {
        if (StringUtils.isEmpty(group.getVkId())) {
            return true;
        }
        for (GroupStatus status : GroupStatus.values()) {
            if (status.name().equals(group.getVkId())) {
                return true;
            }
        }
        return false;
    }

    private Entry<String, String> entry(String name, GroupStatus status) {
        return entry(name, status.name());
    }

    private Entry<String, String> entry(String name, String id) {
        return new AbstractMap.SimpleEntry<String, String>(name, id);
    }

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain.endsWith("/") ? vkDomain : vkDomain + "/";
    }

}
