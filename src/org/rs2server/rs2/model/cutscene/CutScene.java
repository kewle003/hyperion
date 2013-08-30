package org.rs2server.rs2.model.cutscene;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;

/**
 * Created by IntelliJ IDEA.
 * User: Physiologus
 * Date: 4/1/12
 * Time: 12:23 AM
 */
public interface CutScene {
    /**
     * Gets the attribute name.
     */
    public abstract String getAttributeName();
    /**
     * Starts the cutscene.
     *
     * @param player The player.
     */
    public abstract void start(Player player);

    /**
     * Gets the dialogues for the cutscene
     *
     * @param player The player to send dialogue to
     * @param id     The dialogue id(s)
     */
    public abstract void getDialogues(Player player, int id);

    /**
     * Stops / Ends the cutscene.
     *
     * @param player The player.
     */
    public abstract void stop(Player player);


}
