package org.rs2server.rs2.database.impl;

import org.rs2server.rs2.model.PlayerDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by IntelliJ IDEA.
 * User: Physiologus
 * Date: 4/7/12
 * Time: 3:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoginData {
    /**
     * Verifys the user login.
     *
     * @param pd the player details
     * @return Authentication worked or not
     * @throws java.io.IOException error
     */
    public static int authentication(PlayerDetails pd) throws IOException {
        URL url = new URL("http://localhost:8888/smf/rs2/rs2authentication.php?user=" + pd.getName() + "&pass=" + pd.getPassword());
        URLConnection connection = url.openConnection();
        // connection.addRequestProperty("Referer", "http://www.codewithus.net/");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        return line.equalsIgnoreCase("yes") ? 2 : 3;
    }

    /**
     * Gets the rights
     * @param pd The player data
     * @throws IOException error
     */
    public static int information(PlayerDetails pd) throws IOException {
        URL url = new URL("http://localhost:8888/smf/rs2/rs2information.php?user=" + pd.getName());
        URLConnection connection = url.openConnection();
        // connection.addRequestProperty("Referer", "http://www.codewithus.net/");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        int value = Integer.valueOf(line);
        pd.setForumRights(value);
        return value;
    }

    /**
     * Checks if the account has been activated
     * @param pd The player data
     * @throws IOException error
     */
    public static boolean isActivated(PlayerDetails pd) throws IOException {
        URL url = new URL("http://localhost:8888/smf/rs2/rs2activation.php?user=" + pd.getName());
        URLConnection connection = url.openConnection();
        // connection.addRequestProperty("Referer", "http://www.codewithus.net/");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.readLine();
        boolean value = Boolean.valueOf(line);

        return value;
    }

}
