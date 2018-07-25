package com.violet.lib.pattern.agent.local;

import java.sql.SQLException;

/**
 * Created by kan212 on 2018/4/24.
 */

public class UserManagerImpl implements IUserManager {

    private IUserDao userDao = null;

    public UserManagerImpl() {
        userDao = new UserDaoImpl();
    }

    /**
     * 根据ID查询用户
     */
    public User findUser(String id) throws SQLException {
        return userDao.selUser(id);
    }
}
