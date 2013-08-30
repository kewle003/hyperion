package org.rs2server.util;

import java.io.File;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.RS2Server;
import org.rs2server.rs2.model.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigurationParser {

    private static DocumentBuilder builder;
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public static void parse() throws Exception {
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("./game.xml"));
        doc.getDocumentElement().normalize();

        Element game = (Element) doc.getElementsByTagName("GAME").item(0);
        if (game != null) {
            Constants.SERVER_NAME = String.valueOf(game.getAttribute("SERVER_NAME").replace("%revision%", "" + Server.VERSION));
            RS2Server.PORT = Integer.parseInt(game.getAttribute("PORT"));
            Constants.MEMBER_CHECK = Boolean.parseBoolean(game.getAttribute("MEMBERS"));
            Constants.EXP_MODIFIER = Integer.parseInt(game.getAttribute("EXP_MODIFIER"));
            Constants.WEB_AUTH = Boolean.parseBoolean(game.getAttribute("WEB_AUTHENTICATION"));
        }

        Element inGame = (Element) doc.getElementsByTagName("WELCOME_SCREEN").item(0);
        if (inGame != null) {
            Constants.MESSAGE_OF_THE_WEEK_SCREEN = Integer.parseInt(inGame.getAttribute("SCREEN"));
            Constants.MESSAGE_OF_THE_WEEK = String.valueOf(inGame.getAttribute("MESSAGE").replace("%server_name%", Constants.SERVER_NAME));
        }

        Element entityElement = (Element) doc.getElementsByTagName("ENTITY").item(0);
        if (entityElement != null) {
            Constants.MAX_PLAYERS = Integer.parseInt(entityElement.getAttribute("MAX_PLAYERS"));
            Constants.MAX_NPCS = Integer.parseInt(entityElement.getAttribute("MAX_NPCS"));
        }

        Element miscElement = (Element) doc.getElementsByTagName("FILES").item(0);
        if (miscElement != null) {
            Constants.SCRIPTS_DIRECTORY = miscElement.getAttribute("SCRIPTS_DIRECTORY");
            Constants.ITEM_DEFINITIONS_FILE = miscElement.getAttribute("ITEM_DEFINITIONS");
            Constants.EQUIPMENT_DEFINITIONS_FILE = miscElement.getAttribute("EQUIPMENT_DEFINITIONS");
            Constants.NPC_DEFINITIONS_FILE = miscElement.getAttribute("NPC_DEFINITIONS");
            Constants.NPC_COMBAT_DEFINITIONS_FILE = miscElement.getAttribute("NPC_COMBAT_DEFINITIONS");
            Constants.SHOP_DEFINITIONS_FILE = miscElement.getAttribute("SHOP_DEFINITIONS");
            Constants.CUSTOM_OBJECTS_FILE = miscElement.getAttribute("CUSTOM_OBJECTS_DEFINITIONS");
            Constants.ITEM_SPAWNS_FILE = miscElement.getAttribute("ITEM_SPAWNS_FILE");
            Constants.NPC_SPAWNS_FILE = miscElement.getAttribute("NPC_SPAWNS_FILE");
            Constants.DOORS_FILE = miscElement.getAttribute("DOORS_FILE");
            Constants.BOUNDARIES_FILE = miscElement.getAttribute("BOUNDARIES_FILE");
        }

        Element chainsElement = (Element) doc.getElementsByTagName("STAFF").item(0);
        NodeList chainsList = (NodeList) chainsElement.getElementsByTagName("USER");
        if (chainsElement != null) {
            for (int i = 0; i < chainsList.getLength(); i++) {
                Element chainElement = (Element) chainsList.item(i);
                String opcodesString = chainElement.getAttribute("NAME");
                String rights = "PLAYER";
                StringTokenizer st = new StringTokenizer(opcodesString, ", ");
                int offset = 0;
                String[] users = new String[st.countTokens()];
                while (st.hasMoreTokens())
                    users[offset++] = String.valueOf(st.nextToken());
                NodeList listenerList = (NodeList) chainElement.getElementsByTagName("PLAYER");
                for (int n = 0; n < listenerList.getLength(); n++) {
                    Element listenerElement = (Element) listenerList.item(n);
                    rights = Player.Rights.valueOf(listenerElement.getAttribute("RIGHTS")).name();
                }
                for (int n = 0; n < users.length; n++) {
                    if (rights == "MODERATOR") {
                        Constants.MODERATORS.add(opcodesString);
                    } else if (rights == "ADMINISTRATOR") {
                        Constants.ADMINISTRATORS.add(opcodesString);
                    }
                }
            }
        }

    }
}