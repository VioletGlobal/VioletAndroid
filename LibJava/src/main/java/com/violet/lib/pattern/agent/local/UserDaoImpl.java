package com.violet.lib.pattern.agent.local;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kan212 on 2018/4/24.
 */

public class UserDaoImpl implements IUserDao {

    /**
     * 根据ID查询用户
     * @param id
     * @return
     * @throws SQLException
     */
    @Override
    public User selUser(String id) throws SQLException {
        ResultSet resultSet = null;
        String sql = "SELECT * FROM T_USER WHERE USER_ID = ?";
        Connection conn = ConnectionManager.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        User user = null;
        try {
            pst.setString(1, id);
            resultSet = pst.executeQuery();
            if (resultSet.next()) {
                user = getUser(resultSet);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (pst != null) {
                pst.close();
            }
        }
        return user;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getString("USER_ID"));
        user.setName(resultSet.getString("USER_NAME"));
        user.setPassword(resultSet.getString("PASSWORD"));
        user.setContact_tel(resultSet.getString("CONTACT_TEL"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setCreate_date(resultSet.getTimestamp("CREATE_DATE"));
        return user;
    }
}
