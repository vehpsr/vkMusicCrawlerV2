package com.gans.vk.dashboard.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.gans.vk.dao.UserDao;
import com.gans.vk.model.impl.User;

public class SessionManager {

    private User _user = null; // lazy initialization

    @Autowired
    private UserDao _userDao;
    private String _userUrl;

    public User getCurrentUser() {
        if (_user == null) {
            _user = _userDao.getUserByUrl(_userUrl);
        }
        return _user;
    }

    @Required
    public void setUserUrl(String userUrl) {
        _userUrl = userUrl;
    }

    public void setUserDao(UserDao userDao) {
        _userDao = userDao;
    }
}
