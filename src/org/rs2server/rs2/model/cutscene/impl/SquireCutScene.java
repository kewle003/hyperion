package org.rs2server.rs2.model.cutscene.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.NameUtils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Physiologus
 * Date: 4/1/12
 * Time: 7:43 PM
 */
public class SquireCutScene extends AbstractCutScene {

    /**
     * The singleton instance.
     */
    private static final SquireCutScene INSTANCE = new SquireCutScene();

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    public static SquireCutScene getScene() {
        return INSTANCE;
    }

    @Override
    public String getAttributeName() {
        return "squire";
    }

    @Override
    public void start(final Player player) {
        player.getActionSender().sendWindowPane(Constants.MAIN_WINDOW);
        File f = new File("data/savedGames/" + NameUtils.formatNameForProtocol(player.getName()) + ".dat.gz");
        if (!f.exists()) {
            NPC npc = new NPC(NPCDefinition.forId(606), Location.create(2965, 3367, player.getIndex() * 4), null, null, 6);
            World.getWorld().register(npc);
            player.setAttribute("squire", npc);

            World.getWorld().submit(new Tickable(2) {
                @Override
                public void execute() {
                    player.getWalkingQueue().reset();
                    player.getWalkingQueue().addStep(2965, 3365);
                    player.getWalkingQueue().finish();
                }
            });

            World.getWorld().submit(new Tickable(20) {
                @Override
                public void execute() {
                    getDialogues(player, 1);
                    this.stop();
                }
            });
        }
    }

    @Override
    public void getDialogues(Player player, int id) {
        switch (id) {
            case 1:
                player.getActionSender().sendDialogue("Squire", ActionSender.DialogueType.NPC, 606, Animation.FacialAnimation.DEFAULT,
                        "Congratulations, " + player.getName() + "! You have just completed",
                        "the White Knight training course. Now it is time for you",
                        "to venture out into our wide world, and defend it as an",
                        "honourable White Knight.");
                player.getInterfaceState().setNextDialogueId(0, 2);
                break;
            case 2:
                player.getActionSender().sendDialogue("Squire", ActionSender.DialogueType.NPC, 606, Animation.FacialAnimation.DEFAULT,
                        "Here is your complimentary starter pack.");
                player.getInterfaceState().setNextDialogueId(0, 3);
                break;
            case 3:
                stop(player);
                break;
        }
    }

    @Override
    public void stop(Player player) {
        super.stop(player);
        if (player.getAttribute(getAttributeName()) != null) {
            World.getWorld().unregister((NPC) player.getAttribute(getAttributeName()));
            Location teleport = Location.create(2965, 3365, 0);
            player.setLocation(teleport);
            player.setTeleportTarget(teleport);
            player.removeAttribute(getAttributeName());
            player.getActionSender().removeAllInterfaces().sendDefaultChatbox().sendSidebarInterfaces();
        }
    }
}
