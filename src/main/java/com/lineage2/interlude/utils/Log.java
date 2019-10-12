package com.lineage2.interlude.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import l2.authserver.Config;
import l2.authserver.accounts.Account;
import l2.authserver.database.L2DatabaseFactory;
import l2.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger _log = LoggerFactory.getLogger(Log.class);

    public Log() {
    }

    public static void LogAccount(Account account) {
        if (Config.LOGIN_LOG) {
            Connection con = null;
            PreparedStatement statement = null;

            try {
                con = L2DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("INSERT INTO account_log (time, login, ip) VALUES(?,?,?)");
                statement.setInt(1, account.getLastAccess());
                statement.setString(2, account.getLogin());
                statement.setString(3, account.getLastIP());
                statement.execute();
            } catch (Exception var7) {
                _log.error("", var7);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }

        }
    }
}
