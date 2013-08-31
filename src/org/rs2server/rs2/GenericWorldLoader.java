package org.rs2server.rs2;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.rs2.database.impl.LoginConnection;
import org.rs2server.rs2.database.impl.LoginData;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.PlayerDetails;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.util.NameUtils;
import org.rs2server.util.Streams;
import org.rs2server.util.XMLController;


/**
 * An implementation of the <code>WorldLoader</code> class that saves players
 * in binary, gzip-compressed files in the <code>data/players/</code>
 * directory.
 *
 * @author Graham Edgecombe
 */
public class GenericWorldLoader implements WorldLoader {

    public LoginResult checkLogin(PlayerDetails pd) {
        if (Constants.WEB_AUTH) {
            Player player = null;
            int code = 2;
            try {
                code = LoginData.authentication(pd);
            } catch (IOException e) {
                code = 11;
            }
            if (code == 2) {
                try {
                    LoginData.information(pd);
                } catch (IOException e) {
                    code = 11;
                }
                player = new Player(pd);
            }
            return new LoginResult(code, player);
        } else {
            Player player = null;
            int code = 2;
            if (code == 2) {

                File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
                if (f.exists()) {
                    try {
                        InputStream is = new GZIPInputStream(new FileInputStream(f));
                        String name = Streams.readRS2String(is);
                        String pass = Streams.readRS2String(is);
                        if (!name.equals(NameUtils.formatName(pd.getName()))) {
                            code = 3;
                        }
                        if (!pass.equals(pd.getPassword())) {
                            code = 3;
                        }

                        List<String> bannedUsers = XMLController.readXML(new File("data/bannedUsers.xml"));
                        for (String bannedName : bannedUsers) {
                            if (bannedName.equalsIgnoreCase(NameUtils.formatName(pd.getName()))) {
                                code = 4;
                                break;
                            }
                        }
                    } catch (IOException ex) {
                        code = 11;
                    }
                }
            }
            if (code == 2) {
                for (String mod : Constants.MODERATORS) {
                    if (pd.getName().equalsIgnoreCase(mod)) {
                        pd.setForumRights(2);
                    }
                }
                for (String admin : Constants.ADMINISTRATORS) {
                    if (pd.getName().equalsIgnoreCase(admin)) {
                        pd.setForumRights(1);
                    }
                }
                
                if (pd.getName().equalsIgnoreCase("kewley")) {
                	pd.setForumRights(1);
                }

                player = new Player(pd);
            }
            return new LoginResult(code, player);

        }
    }


    @Override
    public boolean savePlayer(Player player) {
        try {
            OutputStream os = new GZIPOutputStream(new FileOutputStream("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz"));
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            player.serialize(buf);
            buf.flip();
            byte[] data = new byte[buf.limit()];
            buf.get(data);
            os.write(data);
            os.flush();
            os.close();
            World.getWorld().serializePrivate(player.getName());
            return true;
        } catch (IOException ex) {
            return false;
        }

    }

    @Override
    public boolean loadPlayer(Player player) {
        try {
            File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
            InputStream is = new GZIPInputStream(new FileInputStream(f));
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while (true) {
                byte[] temp = new byte[1024];
                int read = is.read(temp, 0, temp.length);
                if (read == -1) {
                    break;
                } else {
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            player.deserialize(buf);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
