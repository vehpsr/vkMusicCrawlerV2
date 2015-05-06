package com.gans.vk.service.impl;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gans.vk.dao.UserDao;
import com.gans.vk.dao.UserDao.UserLibData;
import com.gans.vk.json.StatNode;
import com.gans.vk.model.impl.User;
import com.gans.vk.model.impl.User.UserStatus;
import com.gans.vk.service.UserService;

public class UserServiceImpl implements UserService {

    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);

    @Autowired private UserDao _userDao;

    @Override
    public User getByVkId(String vkId) {
        return _userDao.getUserByVkId(vkId);
    }

    @Override
    public User getByUrl(String url) {
        return _userDao.getUserByUrl(url);
    }

    @Override
    public void save(User user) {
        _userDao.save(user);
    }

    @Override
    public List<UserLibData> getRecomendedUserLibData(User user, User target) {
        return _userDao.getRecomendedUserLibData(user, target);
    }

    @Override
    public void importUnique(List<Entry<String, String>> users) {
        _userDao.importUnique(users);
    }

    @Override
    public int getUndiscoveredUsersCount() {
        return _userDao.getUndiscoveredUsersCount();
    }

    @Override
    public List<User> getUndiscoveredUsers(int limit) {
        return _userDao.getUndiscoveredUsers(limit);
    }

    @Override
    public User get(long id) {
        return _userDao.get(id);
    }

    @Override
    public User getRandomUser() {
        return _userDao.getRandomUser();
    }

    @Override
    public StatNode statisticsUserData() {
        int total = _userDao.countAll();
        int undiscovered = _userDao.getUndiscoveredUsersCount();
        List<Entry<String, Integer>> userStatusStatistics = _userDao.userStatusStatistics();

        StatNode root = new StatNode("Total Users");
        root.setVal(total);

        StatNode undiscoveredNode = new StatNode("Undiscovered");
        undiscoveredNode.setVal(undiscovered);
        root.addNode(undiscoveredNode);

        StatNode discoveredNode = new StatNode("Discovered");
        discoveredNode.setVal(total - undiscovered);
        root.addNode(discoveredNode);

        int discoveryFailures = 0;
        for (Entry<String, Integer> userStatus : userStatusStatistics) {
            StatNode discoveredSubNode = new StatNode(userStatus.getKey());
            discoveredSubNode.setVal(userStatus.getValue());
            discoveredNode.addNode(discoveredSubNode);
            discoveryFailures += userStatus.getValue();
        }

        StatNode discoverySuccessNode = new StatNode("Successful");
        discoverySuccessNode.setVal(total - undiscovered - discoveryFailures);
        discoveredNode.addNode(discoverySuccessNode);

        return root;
    }

    @Override
    public void resolve(long userId) {
        User user = get(userId);
        if (user == null) {
            LOG.error("Fail to find user with Id: " + userId);
            return;
        }
        user.setVkId(UserStatus.RESOLVED.name());
        _userDao.save(user);
    }

    public void setUserDao(UserDao userDao) {
        _userDao = userDao;
    }
}
