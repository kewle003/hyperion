package org.rs2server.rs2.model.quests.impl;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Custom werwolf quest :)
 * User: Physiologus
 * Date: 4/11/12
 * Time: 1:26 AM
 * Decided to make a cool custom quest using the quest system I made.
 */
public class WerewolfQuest extends AbstractQuest {

    /**
     * Spawns the npcs
     *
     * @param player The player.
     */
    public void spawnNpcs(Player player) {
        if (player.getQuestStorage().getQuestStage(this) == 3) {
            NPC wolf = new NPC(NPCDefinition.forId(6212), Location.create(2850, 3348, player.getIndex() * 4), null, null, 6);
            World.getWorld().register(wolf);
            wolf.getCombatState().startAttacking(player, true);
        }
    }


    @Override
    public String getAttributeName() {
        return "werewolfQuest";
    }

    @Override
    public String getQuestName() {
        return "Werewolf Vista";
    }

    @Override
    public int getQuestButton() {
        return 14;
    }

    @Override
    public int getFinalStage() {
        return 4;
    }

    @Override
    public int rewardQuestPoints(Player player) {
        return 4;
    }

    @Override
    public boolean hasRequirements(Player player) {
        if (player.getSkills().getLevel(Skills.ATTACK) >= 60
                && player.getSkills().getLevel(Skills.STRENGTH) >= 71) {
            return true;
        }
        return false;
    }

    @Override
    public void start(Player player) {
        super.start(player);
    }

    @Override
    public void getProgress(Player player, boolean show, int progress) {
        super.getProgress(player, show, progress);
        player.getActionSender().sendString(275, 4, player.getSkills().getLevel(Skills.ATTACK) >= 60 ? "<str>60 Attack</str>" : "60 Attack");
        player.getActionSender().sendString(275, 5, player.getSkills().getLevel(Skills.STRENGTH) >= 71 ? "<str>71 Strength</str>" : "71 Strength");

        switch (progress) {
            case 0:
                player.getActionSender().sendString(275, 6, "I could start this quest by talking");
                player.getActionSender().sendString(275, 7, "to Cyrisus down at the harbor in Rellekka.");
                break;
            case 1:
                player.getActionSender().sendString(275, 6, "<str>I could start this quest by talking</str>");
                player.getActionSender().sendString(275, 7, "<str>to Cyrisus down at the harbor in Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "I must me Cyrisus down at Port Sarim Harbor.");
                player.getActionSender().sendString(275, 9, "*Get ready to fight a dangerous werewolf!*");
                break;
            case 2:
            case 3:
                player.getActionSender().sendString(275, 6, "<str>I could start this quest by talking</str>");
                player.getActionSender().sendString(275, 7, "<str>to Cyrisus down at the harbor in Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "<str>I must me Cyrisus down at Port Sarim Harbor.</str>");
                player.getActionSender().sendString(275, 9, "<str>*Get ready to fight a dangerous werewolf!*</str>");
                player.getActionSender().sendString(275, 10, "Head to the Church, kill the wolf, then talk to Cyrisus.</str>");
                break;
            case 4:
                player.getActionSender().sendString(275, 6, "<str>I could start this quest by talking</str>");
                player.getActionSender().sendString(275, 7, "<str>to Cyrisus down at the harbor in Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "<str>I must me Cyrisus down at Port Sarim Harbor.</str>");
                player.getActionSender().sendString(275, 9, "<str>*Get ready to fight a dangerous werewolf!*</str>");
                player.getActionSender().sendString(275, 10, "<str>Head to the Church, kill the wolf, then talk to Cyrisus.</str>");
                player.getActionSender().sendString(275, 10, "<str>I've defeated the Werewolf.</str>");
                break;
        }
    }

    boolean spawned = false;

    @Override
    public void getDialogues(final Player player, int npc, int id) {
        switch (npc) {
            case 5893: //cyrsisus
                switch (id) {
                    case 0:
                        player.getActionSender().sendDialogue("Cyrisus", DialogueType.NPC, npc, FacialAnimation.SAD,
                                "About this wolf.. It's in Entrana, my home place.",
                                "It's scaring everyone away and not much",
                                "people live there now..");
                        player.getInterfaceState().setNextDialogueId(0, 1);
                        break;
                    case 1:
                        player.getActionSender().sendDialogue("Cyrisus", DialogueType.NPC, npc, FacialAnimation.DEFAULT,
                                "I'll help  you fight the wolf, definitely.",
                                "Just meet me at Entrana.",
                                "You can talk to the Monk at Port Sarim to get there.");
                        player.getInterfaceState().setNextDialogueId(0, -1);
                        break;
                }
                return;

            case 5896://stage 2
                final NPC npcd = new NPC(NPCDefinition.forId(5896), Location.create(2840, 3348, player.getIndex() * 4), null, null, 6);
                switch (id) {

                    case 0:
                        World.getWorld().register(npcd);
                        player.getInterfaceState().setNextDialogueId(0, 1);
                        break;
                    case 1:
                        player.getQuestStorage().setQuestStage(this, 3);
                        player.getActionSender().sendDialogue("Cyrisus", DialogueType.NPC, npc, FacialAnimation.MORE_SAD,
                                "I can't do it, it's in the church",
                                "I just can't!");
                        player.getInterfaceState().setNextDialogueId(0, 2);
                        break;
                    case 2:
                        player.getActionSender().sendDialogue("Cyrisus", DialogueType.NPC, npc, FacialAnimation.ALMOST_CRYING,
                                "agh!");
                        spawnNpcs(player);
                        World.getWorld().unregister(npcd);
                        break;
                }
                return;
        }
        return;
    }


    @Override
    public void end(Player player) {
        super.end(player);
    }

    @Override
    public void actionButtons(Player player, int interfaceId, int child, int button) {
    }

    @Override
    public void objectInteraction(ObjectOptions options, int id) {

    }

    @Override
    public void getRewards(Player player) {
        super.getRewards(player);
        player.getActionSender().sendString(277, 8, "Ability to turn into a Werewolf.");
    }

    @Override
    public boolean deathHook(Player player) {
        return true;
    }

    @Override
    public void killHook(Player player, Mob victim) {
        if (victim.isNPC()) {
            NPC npc = (NPC) victim;
            World.getWorld().unregister(npc);
            if (npc.getDefinition().getId() == 6212
                    && player.getQuestStorage().getQuestStage(this) == 3) {
                player.getQuestStorage().setQuestStage(this, 4);
                Item item = new Item(10896, 1);
                if (!player.getInventory().hasRoomFor(item)) {
                    player.getBank().add(item);
                    player.getActionSender().sendMessage("You retrieve a wolf whistle! It's been added to your bank.");
                } else {
                    player.getInventory().add(item);
                    player.getActionSender().sendMessage("You retrieve a wolf whistle!");
                }

                end(player);
                player.setTeleportTarget(Location.create(3048, 3235, 0));
            }

        }
    }
}

