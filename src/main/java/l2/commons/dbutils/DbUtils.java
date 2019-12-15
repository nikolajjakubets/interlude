package l2.commons.dbutils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DbUtils {
    public DbUtils() {
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }

    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }

    }

    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }

    }

    public static void close(Statement stmt, ResultSet rs) throws SQLException {
        close(stmt);
        close(rs);
    }

    public static void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) {
          log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        }

    }

    public static void closeQuietly(Connection conn, Statement stmt) {
        try {
            closeQuietly(stmt);
        } finally {
            closeQuietly(conn);
        }

    }

    public static void closeQuietly(Statement stmt, ResultSet rs) {
        try {
            closeQuietly(stmt);
        } finally {
            closeQuietly(rs);
        }

    }

    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }

    }

    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) {
          log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        }

    }

    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) {
          log.error("closeQuietly: eMessage={}, eClause={} eClass={}", e.getMessage(), e.getCause(), e.getClass());
        }

    }
}
