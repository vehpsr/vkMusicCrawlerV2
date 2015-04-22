package com.gans.vk.processors;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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

public class VkGroupMembersResponseProcessor {

    private static final Log LOG = LogFactory.getLog(VkGroupMembersResponseProcessor.class);
    private static final String GROUP_MEMBER_CONTAINER_CLASS = "fans_fan_name";
    private static final String GROUP_MEMBER_LINK_ELEMENT_SELECTOR = "a.fans_fan_lnk";
    private static final int PEOPLE_ON_PAGE = 60;

    @Autowired private HttpVkConnector _vkConnector;
    private String _vkGroupMembersUrl;
    private String _vkGroupMembersEntityPattern;

    public List<Entry<String, String>> discoverMembersOf(Group group) {
        LOG.info(MessageFormat.format("Discover members for group {0}", group));

        int safetyCounter = 1000;
        int offset = group.getPaginationStart();
        String lastPersonUrl = "";
        List<Entry<String, String>> result = new ArrayList<>();
        while (safetyCounter-- > 0) {
            LinkedList<Entry<String, String>> membersInfo = getMembersInfo(group, offset);
            if (membersInfo.isEmpty()) {
                LOG.info(MessageFormat.format("Empty members list response on offset {0}", offset));
                return Collections.emptyList();
            }

            if (lastPersonUrl.equals(membersInfo.peekLast().getKey())) {
                LOG.info(MessageFormat.format("Same subset at offset {0}", offset));
                return Collections.emptyList();
            } else {
                lastPersonUrl = membersInfo.peekLast().getKey();
            }

            result.addAll(membersInfo);
            offset += PEOPLE_ON_PAGE;
        }
        return result;
    }

    private LinkedList<Entry<String, String>> getMembersInfo(Group group, int offset) {
        String postEntity = MessageFormat.format(_vkGroupMembersEntityPattern, offset, group.getVkId());

        String html = _vkConnector.post(_vkGroupMembersUrl, postEntity);
        html = HtmlUtils.sanitizeHtml(html);
        if (StringUtils.isEmpty(html)) {
            return new LinkedList<>();
        }

        LinkedList<Entry<String, String>> members = new LinkedList<>();
        Document doc = Jsoup.parse(html);
        for (Element element : doc.getElementsByClass(GROUP_MEMBER_CONTAINER_CLASS)) {
            Element name = element.select(GROUP_MEMBER_LINK_ELEMENT_SELECTOR).iterator().next();
            String url = name.attr("href");
            if (StringUtils.isNotEmpty(url)) {
                members.add(new AbstractMap.SimpleEntry<String, String>(url, name.text()));
            }
        }
        return members;
    }

    public void setVkConnector(HttpVkConnector vkConnector) {
        _vkConnector = vkConnector;
    }

    public void setVkGroupMembersUrl(String vkGroupMembersUrl) {
        _vkGroupMembersUrl = vkGroupMembersUrl;
    }

    public void setVkGroupMembersEntityPattern(String vkGroupMembersEntityPattern) {
        _vkGroupMembersEntityPattern = vkGroupMembersEntityPattern;
    }

}
