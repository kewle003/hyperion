package org.rs2server.rs2.model.quests.impl;

/**
 * Custom Dwarf Cannon Quest!
 * User: Phsyiologus
 * Date: 4/11/12
 * Time: 3:38 PM
 * second custom quest. (used for testing mainly)
 */

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.net.ActionSender.DialogueType;

public class DwarfCannonQuest extends AbstractQuest {

    @Override
    public String getAttributeName() {
        return "dwarfCannonQuest";
    }

    @Override
    public String getQuestName() {
        return "Dwarf Cannon";
    }

    @Override
    public int getQuestButton() {
        return 15;
    }

    @Override
    public int getFinalStage() {
        return 4;
    }

    @Override
    public boolean hasRequirements(Player player) {
        return player.getSkills().getLevel(Skills.RANGE) >= 50 ? true : false;
    }

    @Override
    public void start(Player player) {
        super.start(player);
    }

    @Override
    public void getProgress(Player player, boolean show, int progress) {
        super.getProgress(player, show, progress);
        player.getActionSender().sendString(275, 4, hasRequirements(player) ? "<str>50 Range</str>" : "50 Range");
        switch (progress) {
            case 0:
                player.getActionSender().sendString(275, 5, "I could start this quest by talking to");
                player.getActionSender().sendString(275, 6, "the Drunken Dwarf located in the bar at Rellekka.");
                break;
            case 1:
                player.getActionSender().sendString(275, 5, "<str>I could start this quest by talking to</str>");
                player.getActionSender().sendString(275, 6, "<str>the Drunken Dwarf located in the bar at Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "The Dwarf needs help getting his cannon.");
                player.getActionSender().sendString(275, 9, "I should go talk to the Dwarf to find where to get it");
                break;
            case 2:
                player.getActionSender().sendString(275, 5, "<str>I could start this quest by talking to</str>");
                player.getActionSender().sendString(275, 6, "<str>the Drunken Dwarf located in the bar at Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "<str>The Dwarf needs help getting his cannon.</str>");
                player.getActionSender().sendString(275, 9, "<str>I should go talk to the Dwarf to find where to get it</str>");
                player.getActionSender().sendString(275, 10, "Take the items received from the Drunken Dwarf down to");
                player.getActionSender().sendString(275, 11, "Nulodian located at the Dwarf Camp, north of Falador.");
                break;

            case 3:
                player.getActionSender().sendString(275, 5, "<str>I could start this quest by talking to</str>");
                player.getActionSender().sendString(275, 6, "<str>the Drunken Dwarf located in the bar at Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "<str>The Dwarf needs help getting his cannon.</str>");
                player.getActionSender().sendString(275, 9, "<str>I should go talk to the Dwarf to find where to get it</str>");
                player.getActionSender().sendString(275, 10, "<str>Take the items received from the Drunken Dwarf down to</str>");
                player.getActionSender().sendString(275, 11, "<str>Nulodian located at the Dwarf Camp, north of Falador.</str>");
                player.getActionSender().sendString(275, 12, "Nulodian gave me a canon set to give to the Drunken Dwarf.");
                player.getActionSender().sendString(275, 13, "I should head back and talk to the Dwarf.");
                break;
            case 4:
                player.getActionSender().sendString(275, 5, "<str>I could start this quest by talking to</str>");
                player.getActionSender().sendString(275, 6, "<str>the Drunken Dwarf located in the bar at Rellekka.</str>");
                player.getActionSender().sendString(275, 8, "<str>The Dwarf needs help getting his cannon.</str>");
                player.getActionSender().sendString(275, 9, "<str>I should go talk to the Dwarf to find where to get it</str>");
                player.getActionSender().sendString(275, 10, "<str>Take the items received from the Drunken Dwarf down to</str>");
                player.getActionSender().sendString(275, 11, "<str>Nulodian located at the Dwarf Camp, north of Falador.</str>");
                player.getActionSender().sendString(275, 12, "<str>Nulodian gave me a canon set to give to the Drunken Dwarf.</str>");
                player.getActionSender().sendString(275, 13, "<str>I should head back and talk to the Dwarf.</str>");
                break;
        }
    }

    @Override
    public void getDialogues(Player player, int npc, int id) {
        switch (npc) {
            case 956: //drunken dwarf
                switch (id) {
                    case 0:
                        player.getActionSender().sendDialogue("Drunken Dwarf", DialogueType.NPC, 956, Animation.FacialAnimation.SAD,
                                "Could you receive my cannon for me?",
                                "I'm too lazy..");
                        player.getInterfaceState().setNextDialogueId(0, 1);
                        break;
                    case 1:
                        player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, Animation.FacialAnimation.SLEEPY,
                                "Yeah, sure thing..",
                                "What do you want me to do?");
                        player.getInterfaceState().setNextDialogueId(0, 2);
                        break;
                    case 2:
                        player.getActionSender().sendDialogue("Drunken Dwarf", DialogueType.NPC, 956, Animation.FacialAnimation.HAPPY,
                                "Thank you so much!",
                                "Can you give this to Nulodian down at the Dwarf Camp?",
                                "Up north from Falador.");
                        player.getInterfaceState().setNextDialogueId(0, 3);
                        break;
                    case 3:
                        player.getActionSender().sendInformationBox(DialogueType.MESSAGE_OPTION, 1, "The Dwarf hands you a toolkit and few other items.");
                        Item addItems[] = {new Item(1), new Item(3)};
                        for (Item item : addItems) {
                            if (!player.getInventory().hasRoomFor(item)) {
                                player.getActionSender().sendMessage("You don't have enough room.");
                                return;
                            }
                            player.getQuestStorage().setQuestStage(this, 2);
                            player.getInventory().add(item);
                        }
                        break;
                    //Stage 2
                    case 4:
                        player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, Animation.FacialAnimation.CALM_1,
                                "Here is your cannon that Nulodian gave me!");
                        player.getInterfaceState().setNextDialogueId(0, 5);
                        break;
                    case 5:
                        player.getActionSender().sendDialogue("Drunken Dwarf", DialogueType.NPC, 956, Animation.FacialAnimation.HAPPY,
                                "Wow, thank you, " + player.getName() + "!");
                        Item delItems[] = {new Item(6), new Item(8), new Item(10), new Item(12)};
                        for (Item item : delItems) {
                            player.getQuestStorage().setQuestStage(this, 4);
                            player.getInventory().remove(item);
                        }
                        player.getInterfaceState().setNextDialogueId(0, 6);
                        end(player);
                        break;
                }
                return;
            case 209:   //Nulodian
                switch (id) {
                    case 0:
                        player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, Animation.FacialAnimation.CALM_1,
                                "Hey, I was told to give you these items..");
                        player.getInterfaceState().setNextDialogueId(0, 1);
                        break;
                    case 1:
                        player.getActionSender().sendInformationBox(DialogueType.MESSAGE_OPTION, -1,
                                "*Nulodian looks at the items*");
                        player.getInterfaceState().setNextDialogueId(0, 2);
                        break;
                    case 2:
                        player.getActionSender().sendDialogue("Nulodian", DialogueType.NPC, 209, Animation.FacialAnimation.SAD,
                                "Ah yes, i'm sure the Drunken dwarf sent you here.",
                                "Please, hand him these.");
                        player.getInterfaceState().setNextDialogueId(0, 3);
                        break;
                    case 3:
                        player.getActionSender().sendInformationBox(DialogueType.MESSAGE_OPTION, 1, "Nulodian hands you a cannon set.");
                        Item addItems[] = {new Item(6), new Item(8), new Item(10), new Item(12)};
                        for (Item item : addItems) {
                            if (!player.getInventory().hasRoomFor(item)) {
                                player.getActionSender().sendMessage("You don't have enough room.");
                                return;
                            }
                            player.getQuestStorage().setQuestStage(this, 3);
                            player.getInventory().add(item);
                        }
                        break;
                }
                break;
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
        player.getActionSender().sendString(277, 8, "Ability to use and buy the Dwarf Cannon.");
    }

    @Override
    public int rewardQuestPoints(Player player) {
        return 2;
    }

    @Override
    public boolean deathHook(Player player) {
        return true;
    }

    @Override
    public void killHook(Player player, Mob victim) {

    }
}
