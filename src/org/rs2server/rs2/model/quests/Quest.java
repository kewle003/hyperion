package org.rs2server.rs2.model.quests;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;

/**
 * The Quest Interface
 * User: Physiologus
 * Date: 4/10/12
 * Time: 10:01 PM
 * Stuff from the minigame base (death, kill hooks) are used here also..
 * ONLY because it is required ok
 */
public interface Quest {
    /**
     * Gets the attribute name.
     */
    public abstract String getAttributeName();

    /**
     * Gets quest name (for quest sidebar)
     */
    public abstract String getQuestName();

    /**
     * The quest interface button.
     * Also known as the quest Id.
     */
    public abstract int getQuestButton();

    /**
     * The last stage
     */
    public abstract int getFinalStage();

    /**
     * The quest points to award.
     */
    public abstract int rewardQuestPoints(Player player);

    /**
     * Gets the requirements for the quest.
     */
    public abstract boolean hasRequirements(Player player);

    /**
     * Begins the quest.
     *
     * @param player The player.
     */
    public abstract void start(Player player);

    /**
     * Gets the progress of the quest
     *
     * @param player   The player
     * @param show     {@code true} Shows interface {@code false}  doesn't show
     * @param progress The progress stage
     */
    public abstract void getProgress(Player player, boolean show, int progress);

    /**
     * Gets the dialogues for the quest
     *
     * @param player The player to send dialogue to
     * @param npc    The npc.
     * @param id     The dialogue id(s)
     */
    public abstract void getDialogues(Player player, int npc, int id);

    /**
     * Ends the quest.
     *
     * @param player The player.
     */
    public abstract void end(Player player);

    /**
     * The custom button actions.
     *
     * @param player      The player.
     * @param interfaceId The interface id.
     * @param child       The child id.
     * @param button      The button Id.
     */
    public abstract void actionButtons(Player player, int interfaceId, int child, int button);

    /**
     * The custom object actions.
     *
     * @param options The options.
     * @param id      The object id.
     */
    public abstract void objectInteraction(ObjectOptions options, int id);

    /**
     * Adds rewards.
     *
     * @param player The player.
     */
    public abstract void getRewards(Player player);

    /**
     * Performs checks when a player dies in the minigame.
     * @param player The player.
     * @return True = don't drop loot, false = do drop loot.
     */
    public abstract boolean deathHook(Player player);

    /**
     * Performs checks for when the player kills another mob.
     * @param player The player.
     * @param victim The victim.
     */
    public abstract void killHook(Player player, Mob victim);

    enum ObjectOptions {
        ONE,
        TWO,
        THREE;
    }
}
