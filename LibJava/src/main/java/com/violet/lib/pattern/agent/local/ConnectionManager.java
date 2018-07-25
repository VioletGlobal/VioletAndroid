package com.violet.lib.pattern.agent.local;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by kan212 on 2018/4/24.
 */

public class ConnectionManager {

    private static ThreadLocal<Connection>
            threadConn=new ThreadLocal<>();

    private ConnectionManager() {
    }
    /**
     * @return 获取数据库连接
     * @author mine_song
     */
    public static Connection getConnection() {
        Connection conn = threadConn.get();
        if (conn == null) {
            try {
                Class.forName("java.sql.Driver");
                conn = DriverManager.getConnection("jdbc:mysql:///erpdb","root","123");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            threadConn.set(conn);
        }
        return conn;
    }

    /**
     * 设置事务手动提交
     * @param conn
     */
    public static void benigTransction(Connection conn) {
        try {
            if (conn != null) {
                if (conn.getAutoCommit()) {
                    conn.setAutoCommit(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交事务
     * @param conn
     */
    public static void endTransction(Connection conn) {
        try {
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.commit();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Connection的原始状态 即设置事务手动提交
     * @param conn
     */
    public static void recoverTransction(Connection conn) {
        try {
            if (conn != null) {
                if (conn.getAutoCommit()) {
                    conn.setAutoCommit(false);
                } else {
                    conn.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生异常回滚事务
     * @param conn
     */
    public static void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接,并将其从当前线程删除
     */
    public static void close() {
        Connection conn = threadConn.get();
        if (conn != null) {
            try {
                conn.close();
                conn = null;
                threadConn.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
