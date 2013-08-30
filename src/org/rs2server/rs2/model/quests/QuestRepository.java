package org.rs2server.rs2.model.quests;

import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.quests.impl.DwarfCannonQuest;
import org.rs2server.rs2.model.quests.impl.WerewolfQuest;

import java.util.HashMap;
import java.util.Map;

/**
 * Works as a quest database and handles quest-related events.
 *
 * @author Emperor
 */
public class QuestRepository {

    /**
     * The amount of quests available.
     */
    public static final int AMOUNT_OF_QUESTS = 136;

    /**
     * The mapping holding all quest data.
     */
    public static final Map<Integer, Quest> QUEST_DATABASE = new HashMap<Integer, Quest>();

    /**
     * Initializes the quests.
     *
     * @return {@code True} if succesful, {@code false} if not.
     */
    public static boolean init() {
        return true;
    }

    static {
        QUEST_DATABASE.put(14, new WerewolfQuest());
        QUEST_DATABASE.put(15, new DwarfCannonQuest());
    }

    /**
     * Grabs a quest from the mapping.
     *
     * @param buttonId The quest button id.
     * @return The {@code Quest} which id matches the id given,
     *         or {@code null} if the quest didn't exist.
     */
    public static Quest get(int buttonId) {
        return QUEST_DATABASE.get(buttonId);
    }

    /**
     * Handles the quest side bar interfaces and name
     *
     * @param player The player.
     */
    public static void handle(Player player) {
        for (Quest quest : QUEST_DATABASE.values()) {
            String questName = quest.getQuestName();
            String line = questName;

            if (player.getQuestStorage().hasStarted(quest)) {
                line = "<col=d7e335>" + questName + "</col>";
            } if (player.getQuestStorage().hasFinished(quest)) {
                line = "<col=8f510>" + questName + "</col>";
            }
            player.getActionSender().sendString(274, quest.getQuestButton(), line);
        }
    }
}