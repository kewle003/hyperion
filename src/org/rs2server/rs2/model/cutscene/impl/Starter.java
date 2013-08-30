package org.rs2server.rs2.model.cutscene.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Created by IntelliJ IDEA.
 * User: Physiologus
 * Date: 4/1/12
 * Time: 12:32 AM
 */
public class Starter extends AbstractCutScene {

    /**
     * The singleton instance.
     */
    private static final Starter INSTANCE = new Starter();

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    public static Starter getScene() {
        return INSTANCE;
    }

    @Override
    public String getAttributeName() {
        return "starter";
    }

    public void spawnNPCs(Player player) {
        //7=SouthEast 6=South 5=SouthWest 4=East 3=West 2=NorthEast 1=North 0=NorthWest
        int height = player.getIndex() * 4;
        NPC one = new NPC(NPCDefinition.forId(1274), Location.create(2659, 3678, height), null, null, 6);
        NPC two = new NPC(NPCDefinition.forId(1275), Location.create(2658, 3679, height), null, null, 6);
        NPC three = new NPC(NPCDefinition.forId(1276), Location.create(2657, 3680, height), null, null, 6);
        NPC four = new NPC(NPCDefinition.forId(1277), Location.create(2661, 3679, height), null, null, 6);
        NPC m = new NPC(NPCDefinition.forId(2477), Location.create(2656, 3682, height), null, null, 6);

        World.getWorld().register(one);
        World.getWorld().register(two);
        World.getWorld().register(three);
        World.getWorld().register(four);
        World.getWorld().register(m);

        m.face(player.getLocation());

        player.setAttribute(getAttributeName(), one);
        player.setAttribute(getAttributeName(), two);
        player.setAttribute(getAttributeName(), three);
        player.setAttribute(getAttributeName(), four);
        player.setAttribute(getAttributeName(), m);

    }

    @Override
    public void start(final Player player) {
        super.start(player);
        player.getActionSender().removeAllInterfaces();
        player.setTeleportTarget(Location.create(2659, 3683, player.getIndex() * 4));

        spawnNPCs(player);

        /*
        World.getWorld().submit(new Tickable(2) {
            @Override
            public void execute() {
                player.getWalkingQueue().reset();
                player.getWalkingQueue().addStep(2965, 3366);
                player.getWalkingQueue().finish();
            }
        });         */

        World.getWorld().submit(new Tickable(3) {
            @Override
            public void execute() {
                getDialogues(player, 1);
                this.stop();
            }
        });
    }

    public void getDialogues(final Player player, int dialogue) {
        switch (dialogue) {
            case 1:
                player.getActionSender().sendDialogue("Introducer", ActionSender.DialogueType.NPC, 2477, Animation.FacialAnimation.HAPPY,
                        "Hello, " + player.getName() + "!", "Welcome to " + Constants.SERVER_NAME + ".", "How are you?");
                player.getInterfaceState().setNextDialogueId(0, 2);
                break;
            case 2:
                player.playAnimation(Animation.THINKING);
                World.getWorld().submit(new Tickable(1) {
                    @Override
                    public void execute() {
                        player.getActionSender().sendDialogue(player.getName(), ActionSender.DialogueType.PLAYER, 1, Animation.FacialAnimation.CALM_1,
                                "What am I doing here--");
                        player.getInterfaceState().setNextDialogueId(0, 3);
                        this.stop();
                    }
                });
                break;
            case 3:
                player.getActionSender().sendDialogue("Guide", ActionSender.DialogueType.NPC, 2477, Animation.FacialAnimation.HAPPY,
                        "Rellekka is the home of " + Constants.SERVER_NAME + ". Anything you need",
                        "is around you or maybe far out, but don't worry!",
                        "The people (NPCs) around here can give you help",
                        "if you just ask!");
                player.getInterfaceState().setNextDialogueId(0, 4);
                break;
            case 4:
                player.getActionSender().sendDialogue(player.getName(), ActionSender.DialogueType.PLAYER, 1, Animation.FacialAnimation.CALM_1,
                        "I'll check it out.. Thank you.");
                player.getInterfaceState().setNextDialogueId(0, 5);
                break;
            case 5:
                player.getActionSender().sendInformationBox(ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 995,
                        "300,000 Gold pieces have been added into your bank.");
                player.getBank().add(new Item(995, 300000));
                player.getInterfaceState().setNextDialogueId(0, 6);
                break;
            case 6:
                player.getActionSender().sendDialogue("Guide", ActionSender.DialogueType.NPC, 2477, Animation.FacialAnimation.HAPPY,
                        "The longer you play / the more you post on the forums,",
                        "the more income you get for logging in daily.",
                        "Please follow the rules, and have fun playing!");
                player.getInterfaceState().setNextDialogueId(0, 7);
                break;
            case 7:
                stop(player);
                break;
        }
    }

    @Override
    public void stop(Player player) {
        if (player.getAttribute(getAttributeName()) != null) {
            super.stop(player);
            World.getWorld().unregister((NPC) player.getAttribute(getAttributeName()));
            Location teleport = Entity.DEFAULT_LOCATION;
            player.setLocation(teleport);
            player.setTeleportTarget(teleport);
            player.removeAllAttributes();
            player.getActionSender().removeAllInterfaces().sendDefaultChatbox().sendSidebarInterfaces();
        }
    }

}
