package com.gans.vk.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.model.impl.Group;
import com.gans.vk.processors.VkGroupInfoResponseProcessor;
import com.gans.vk.processors.VkGroupMembersResponseProcessor;
import com.gans.vk.service.GroupDiscoveryService;
import com.gans.vk.service.GroupService;
import com.gans.vk.service.UserService;

public class GroupDiscoveryServiceImpl implements GroupDiscoveryService {

    private static final Log LOG = LogFactory.getLog(GroupDiscoveryServiceImpl.class);

    @Autowired private GroupService _groupService;
    @Autowired private UserService _userService;
    @Autowired private VkGroupInfoResponseProcessor _vkGroupProcessor;
    @Autowired private VkGroupMembersResponseProcessor _vkGroupMembersProcessor;

    @Override
    public void discoverGroupByUserUrl(String vkUrl, boolean forceUpdate) {
        if (StringUtils.isEmpty(vkUrl)) {
            LOG.info("vkUrl is empty");
            return;
        }

        Group group = _groupService.gerByUrl(vkUrl);
        if (group == null) {
            group = new Group();
            group.setUrl(vkUrl);
        }

        Entry<String, String> groupInfo = _vkGroupProcessor.getGroupInfo(vkUrl);

        group.setName(groupInfo.getKey());
        group.setVkId(groupInfo.getValue());
        if (forceUpdate) {
            group.setPaginationStart(0);
        }

        if (VkGroupInfoResponseProcessor.hasInvalidGroupStatus(group)) {
            LOG.info(MessageFormat.format("Fail to discover members of group {0}", group));
            return;
        }

        List<Entry<String, String>> users = _vkGroupMembersProcessor.discoverMembersOf(group);

        _groupService.save(group);
        _userService.importUnique(users);
    }

    public void setGroupService(GroupService groupService) {
        _groupService = groupService;
    }

    public void setVkGroupProcessor(VkGroupInfoResponseProcessor vkGroupProcessor) {
        _vkGroupProcessor = vkGroupProcessor;
    }

    public void setVkGroupMembersProcessor(VkGroupMembersResponseProcessor vkGroupMembersProcessor) {
        _vkGroupMembersProcessor = vkGroupMembersProcessor;
    }

}
