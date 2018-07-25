package com.violet.lib.pattern.agent.local;

import java.sql.SQLException;

/**
 * Created by kan212 on 2018/4/24.
 */

public interface IUserDao {
    public User selUser(String id) throws SQLException;
}
