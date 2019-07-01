package com.how2java.tmall.service;

import com.how2java.tmall.dao.UserDAO;
import com.how2java.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: tyk
 * @Date: 2019/5/17 10:19
 * @Description:
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    public boolean isExit(String name) {
        User user = getName(name);
        return null != user;
    }

    public User getName(String name) {
        return userDAO.findByName(name);
    }

    public void addUser(User user) {
        userDAO.save(user);
    }
}
