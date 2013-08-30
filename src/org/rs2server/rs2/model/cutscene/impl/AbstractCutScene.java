package org.rs2server.rs2.model.cutscene.impl;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.cutscene.CutScene;

/**
 * Created by IntelliJ IDEA.
 * User: Physiologus
 * Date: 4/1/12
 * Time: 12:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractCutScene implements CutScene {

    @Override
    public String getAttributeName() {
        return null;
    }

    @Override
    public void start(Player player) {
        player.inCutScene(true);
        player.setAttribute("cutScene", true);
    }

    @Override
    public void getDialogues(Player player, int id) {

    }

    @Override
    public void stop(Player player) {
        player.inCutScene(false);
        player.setAttribute("cutScene", false);
    }
}
