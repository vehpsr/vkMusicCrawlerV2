package com.gans.vk.service.impl;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.model.impl.Group;
import com.gans.vk.processors.VkGroupInfoResponseProcessor;
import com.gans.vk.service.GroupDiscoveryService;
import com.gans.vk.service.GroupService;

public class GroupDiscoveryServiceImpl implements GroupDiscoveryService {

    private static final Log LOG = LogFactory.getLog(GroupDiscoveryServiceImpl.class);

    @Autowired private GroupService _groupService;
    @Autowired private VkGroupInfoResponseProcessor _vkGroupProcessor;

    @Override
    public void discoverGroupByUserUrl(String vkUrl, boolean forceUpdate) {
        if (StringUtils.isEmpty(vkUrl)) {
            LOG.info("vkUrl is empty");
            return;
        }

        Group group = _groupService.gerByUrl(vkUrl);
        if (!forceUpdate && (group != null && StringUtils.isNotEmpty(group.getUrl()))) {
            LOG.info(MessageFormat.format("Group with url {0} already exists in system", vkUrl));
            return;
        }

        if (group == null) {
            group = new Group();
            group.setUrl(vkUrl);
        }

        _groupService.save(group);
    }

    public void setGroupService(GroupService groupService) {
        _groupService = groupService;
    }

    public void setVkGroupProcessor(VkGroupInfoResponseProcessor vkGroupProcessor) {
        _vkGroupProcessor = vkGroupProcessor;
    }

}
