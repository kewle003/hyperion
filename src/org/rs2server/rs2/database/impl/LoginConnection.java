package org.rs2server.rs2.database.impl;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.rs2server.rs2.database.ConnectionConfig;
import org.rs2server.rs2.database.ConnectionDriver;
import org.rs2server.rs2.model.PlayerDetails;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.rs2.util.TextUtils;


public class LoginConnection {

    private static final LoginConnection connection = new LoginConnection();

    private LoginConnection() {
    }

    public static LoginConnection getSingleton() {
        return connection;
    }

    static {
        try {
            new ConnectionDriver("com.mysql.jdbc.Driver", new ConnectionConfig("jdbc:mysql://localhost:8889/smf", "root", "root"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:jdc:jdcpool");
    }


    public int getRights(PlayerDetails pd) throws RemoteException {
        System.out.println("Getting rights");
        if (pd == null)
            throw new RemoteException("Player Details cannot be null.");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = getConnection();
            statement = con.prepareStatement("SELECT usergroup FROM mybb_users WHERE uid = ?");
            statement.setInt(1, pd.getUserId());
            statement.executeQuery();
            result = statement.getResultSet();
            if (result.next()) {
                int usergroupId = result.getInt(1);
                pd.setForumRights(usergroupId);
                return usergroupId;
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            getRights(pd);
        } finally {
            try {
                if (con != null)
                    con.close();
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public boolean checkStrikes(PlayerDetails pd) throws RemoteException {
        System.out.println("Checking strikes");
        if (pd == null)
            throw new RemoteException("Player Details cannot be null.");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = getConnection();
            statement = con.prepareStatement("SELECT COUNT(*) AS strikes, MAX(striketime) AS lasttime FROM tbl_strikes WHERE strikeip = ? AND UPPER(username) = ?");
            String hostIp = ((InetSocketAddress) pd.getSession().getRemoteAddress()).getAddress().getHostAddress();
            statement.setString(1, hostIp);
            statement.setString(2, pd.getName().toUpperCase());
            statement.executeQuery();
            result = statement.getResultSet();
            if (result.next()) {
                int strikes = result.getInt(1);
                int lastStrike = result.getInt(2);
                Calendar cal = Calendar.getInstance();
                int time = (int) (cal.getTimeInMillis() / 1000);
                if (strikes >= 5 && lastStrike > time - 900) {
                    return true;
                }
            }
        } catch (SQLException sqe) {
            checkStrikes(pd);
        } finally {
            try {
                if (con != null)
                    con.close();
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean checkManualBan(PlayerDetails pd) throws RemoteException {
        System.out.println("Checking ban");
        if (pd == null)
            throw new RemoteException("Player Details cannot be null.");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = getConnection();
            statement = con.prepareStatement("SELECT gid FROM mybb_banned WHERE uid = ?");
            statement.setString(1, pd.getName().toUpperCase());
            statement.executeQuery();
            result = statement.getResultSet();
            if (result.next()) {
                int userId = result.getInt(1);
                statement = con.prepareStatement("SELECT bandate, liftdate FROM tbl_userban WHERE userid = ?");
                statement.setInt(1, userId);
                statement.executeQuery();
                ResultSet bancheck = statement.getResultSet();
                if (bancheck.next()) {
                    int banDate = bancheck.getInt(1);
                    int liftDate = bancheck.getInt(2);
                    Calendar cal = Calendar.getInstance();
                    int time = (int) (cal.getTimeInMillis() / 1000);
                    if (time < liftDate && time > banDate) {
                        return true;
                    }
                }
            }
        } catch (SQLException sqe) {
            sqe.printStackTrace();
            checkManualBan(pd);
        } finally {
            try {
                if (con != null)
                    con.close();
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean verifySignedup(PlayerDetails pd) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("Verifying sign up");
        if (pd == null)
            throw new RemoteException("Player Details cannot be null.");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = getConnection();
            statement = con.prepareStatement("SELECT id_member FROM smf_members WHERE LOWER(member_name) = ?");
            statement.setString(1, NameUtils.formatName(pd.getName()).toLowerCase());
            statement.executeQuery();
            result = statement.getResultSet();
            if (result.next()) {
                pd.setUserId(result.getInt(1));
                return true;
            }
        } catch (SQLException sqe) {
            verifySignedup(pd);
        } finally {
            try {
                if (con != null)
                    con.close();
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public boolean verifyLogin(PlayerDetails pd) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException {
        System.out.println("Verifying login");
        if (pd == null)
            throw new RemoteException("Player Details cannot be null.");
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            con = getConnection();
            statement = con.prepareStatement("SELECT uid, username, password, salt FROM mybb_users WHERE UPPER(username) = ?");
            statement.setString(1, pd.getName().toUpperCase());
            statement.executeQuery();
            result = statement.getResultSet();
            if (result.next()) {
                @SuppressWarnings("unused")
                String resultUsername = result.getString(2).toLowerCase();
                String resultPassword = result.getString(3).toLowerCase();
                String resultSalt = result.getString(4);

                String hashedPassword = TextUtils.MD5(pd.getPassword().toLowerCase());
                hashedPassword = TextUtils.MD5(TextUtils.MD5(resultSalt) + TextUtils.MD5(hashedPassword));
                System.out.println(hashedPassword);
                if (resultPassword.equals(hashedPassword)) {
                    return true;
                } else {
                    return false;
                }/* else {
                         String hostIp = ((InetSocketAddress) pd.getSession().getRemoteAddress()).getAddress().getHostAddress();
                         Calendar cal = Calendar.getInstance();
                         int time = (int) (cal.getTimeInMillis() / 1000);
                         statement = con.prepareStatement("INSERT INTO tbl_strikes(striketime, strikeip, username) VALUES (?, ?, ?)");
                         statement.setInt(1, time);
                         statement.setString(2, hostIp);
                         statement.setString(3, pd.getName().toUpperCase());
                         statement.executeUpdate();
                     }       */
            }

        } catch (SQLException sqe) {
            verifyLogin(pd);
        } finally {
            try {
                if (con != null)
                    con.close();
                if (statement != null)
                    statement.close();
                if (result != null)
                    result.close();
            } catch (Exception e) {
            }
        }

        return false;
    }

}
