package org.rs2server.rs2.model;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.model.quests.QuestRepository;
import org.rs2server.rs2.model.quests.impl.DwarfCannonQuest;
import org.rs2server.rs2.model.quests.impl.WerewolfQuest;
import org.rs2server.rs2.net.ActionSender.DialogueType;


public class DialogueManager {
    public static void openDialogue(final Player player, int dialogueId) {
        if (dialogueId == -1) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            player.getInterfaceState().setNextDialogueId(i, -1);
        }
        player.getInterfaceState().setOpenDialogueId(dialogueId);
        NPC npc = (NPC) player.getInteractingEntity();
        Item skillcape = null;
        Item hood = null;

        if (player.getQuestStorage().hasStarted(new DwarfCannonQuest())) {
            if (npc.getDefinition().getId() == 956 || npc.getDefinition().getId() == 209) {
                new DwarfCannonQuest().getDialogues(player, npc.getDefinition().getId(), dialogueId);
                return;
            }
        }
        if (player.getQuestStorage().hasStarted(new WerewolfQuest())) {
            if (npc.getDefinition().getId() >= 5893 && npc.getDefinition().getId() <= 5897) {
                new WerewolfQuest().getDialogues(player, npc.getDefinition().getId(), dialogueId);
                return;
            }
        }

        if (player.inCutScene()) {
            player.getCutScene().getDialogues(player, dialogueId);
            return;
        }

        switch (dialogueId) {
            case 0:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Good day. How may I help you?");
                player.getInterfaceState().setNextDialogueId(0, 1);
                break;
            case 1:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "I'd like to access my bank account, please.",
                        "I'd like to change my PIN please.",
                        "What is this place?");
                player.getInterfaceState().setNextDialogueId(0, 2);
                player.getInterfaceState().setNextDialogueId(1, 3);
                player.getInterfaceState().setNextDialogueId(2, 5);
                break;
            case 2:
                player.getActionSender().removeChatboxInterface();
                Bank.open(player);
                break;
            case 3:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "I'd like to set/change my PIN please.");
                player.getInterfaceState().setNextDialogueId(0, 4);
                break;
            case 4:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "We currently do not offer bank PINs, sorry.");
                break;
            case 5:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "What is this place?");
                player.getInterfaceState().setNextDialogueId(0, 6);
                break;
            case 6:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "This is a branch of the Bank of " + Constants.SERVER_NAME + ". We have",
                        "branches in many towns.");
                player.getInterfaceState().setNextDialogueId(0, 7);
                break;
            case 7:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "And what do you do?",
                        "Didn't you used to be called the Bank of Varrock?");
                player.getInterfaceState().setNextDialogueId(0, 8);
                player.getInterfaceState().setNextDialogueId(1, 10);
                break;
            case 8:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "And what do you do?");
                player.getInterfaceState().setNextDialogueId(0, 9);
                break;
            case 9:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "We will look after your items and money for you.",
                        "Leave your valuables with us if you want to keep them",
                        "safe.");
                break;
            case 10:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Didn't you used to be called the Bank of Varrock?");
                player.getInterfaceState().setNextDialogueId(0, 11);
                break;
            case 11:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Yes we did, but people kept on coming into our",
                        "branches outside of Varrock and telling us that our",
                        "signs were wrong. They acted as if we didn't know",
                        "what town we were in or something.");
                break;
            case 12:
                player.getActionSender().sendDialogue("Choose spellbook:", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Standard",
                        "Ancient",
                        "Cancel");
                player.getInterfaceState().setNextDialogueId(0, 13);
                player.getInterfaceState().setNextDialogueId(1, 14);
                player.getInterfaceState().setNextDialogueId(2, 15);
                break;
            case 13:
                player.getCombatState().setSpellbookSwap(true);
                MagicCombatAction.executeSpell(Spell.SPELLBOOK_SWAP, player, player, 0);
                player.getActionSender().removeAllInterfaces();
                break;
            case 14:
                player.getCombatState().setSpellbookSwap(true);
                MagicCombatAction.executeSpell(Spell.SPELLBOOK_SWAP, player, player, 1);
                player.getActionSender().removeAllInterfaces();
                break;
            case 15:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hello there.");
                player.getInterfaceState().setNextDialogueId(0, 16);
                break;
            case 16:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello traveller.");
                player.getInterfaceState().setNextDialogueId(0, 17);
                break;
            case 17:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Where am I?",
                        "Who are you?",
                        "Goodbye.");
                player.getInterfaceState().setNextDialogueId(0, 18);
                player.getInterfaceState().setNextDialogueId(1, 20);
                player.getInterfaceState().setNextDialogueId(2, 28);
                break;
            case 18:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Where am I?");
                player.getInterfaceState().setNextDialogueId(0, 19);
                break;
            case 19:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "You are in " + Constants.SERVER_NAME + ". Through quests and",
                        "adventures you may learn the ancient origins of",
                        "this mysterious land, but until then,",
                        "all I can do is offer you help.");
                break;
            case 20:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Who are you?");
                player.getInterfaceState().setNextDialogueId(0, 21);
                break;
            case 21:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I am Mawnis Burowgar, leader of Neitiznot. I am here",
                        "to guide travellers like you through their own adventure.",
                        "Would you like help with anything?");
                player.getInterfaceState().setNextDialogueId(0, 22);
                break;
            case 22:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Where can I get basic supplies?",
                        "Where can I make money?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 23);
                player.getInterfaceState().setNextDialogueId(1, 25);
                player.getInterfaceState().setNextDialogueId(2, 27);
                break;
            case 23:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Where can I get basic supplies?");
                player.getInterfaceState().setNextDialogueId(0, 24);
                break;
            case 24:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "There are many people on this island who will help you.",
                        "Talk to various characters around the island who will give",
                        "you starting items. Also check the shops near by for",
                        "any good deals, and possibly some free items.");
                break;
            case 25:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Where can I make money?");
                player.getInterfaceState().setNextDialogueId(0, 26);
                break;
            case 26:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "The Wise Old Man often helps new comers to make",
                        "some small amounts of money. Other than that, you",
                        "should train your skills and sell anything you make",
                        "to shops or other players for profit.");
                break;
            case 27:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "No thank you.");
                break;
            case 28:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Goodbye.");
                break;
            case 29:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hello.");
                player.getInterfaceState().setNextDialogueId(0, 30);
                break;
            case 30:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello there young warrior. I am Harlan, master",
                        "of melee. What can I do for you?");
                player.getInterfaceState().setNextDialogueId(0, 31);
                break;
            case 31:
                if (player.getSkills().getLevelForExperience(Skills.DEFENCE) > 98) {
                    player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                            "Do you have any supplies for me?",
                            "Talk about defence skillcape.",
                            "Nothing, thanks.");
                    player.getInterfaceState().setNextDialogueId(0, 32);
                    player.getInterfaceState().setNextDialogueId(1, 52);
                    player.getInterfaceState().setNextDialogueId(2, 34);
                } else {
                    player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                            "Do you have any supplies for me?",
                            "Nothing, thanks.");
                    player.getInterfaceState().setNextDialogueId(0, 32);
                    player.getInterfaceState().setNextDialogueId(1, 34);
                }
                break;
            case 32:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Do you have any supplies for me?");
                player.getInterfaceState().setNextDialogueId(0, 33);
                break;
            case 33:
                if (player.getSkills().getLevel(Skills.RANGE) < 20) {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "Ofcourse! I am always eager to help young little",
                            "warriors like yourself. Take this sword and this shield",
                            "and train your melee. But make sure you use",
                            "better armour once you level up.");
                    player.getInventory().add(new Item(9703));
                    player.getInventory().add(new Item(9704));
                } else {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "I'm sorry, I can't help you at all. You should be",
                            "using better equipment at your level.");
                }
                break;
            case 34:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Nothing, thanks.");
                break;
            case 35:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hi.");
                player.getInterfaceState().setNextDialogueId(0, 36);
                break;
            case 36:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Heya! I'm Nemarti. What can I do for you?");
                player.getInterfaceState().setNextDialogueId(0, 37);
                break;
            case 37:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Do you have any supplies for me?",
                        "Nothing, thanks.");
                player.getInterfaceState().setNextDialogueId(0, 38);
                player.getInterfaceState().setNextDialogueId(1, 34);
                break;
            case 38:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Do you have any supplies for me?");
                player.getInterfaceState().setNextDialogueId(0, 39);
                break;
            case 39:
                if (player.getSkills().getCombatLevel() < 20) {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "Sure thing, I'm always helping young arrow slingers!",
                            "Here you go.");
                    player.getInventory().add(new Item(9705));
                    player.getInventory().add(new Item(9706, 50));
                } else {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "I'm sorry, I can't help you at all. You should be",
                            "using better equipment at your level.");
                }
                break;
            case 40:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hello there.");
                player.getInterfaceState().setNextDialogueId(0, 41);
                break;
            case 41:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello young sage. What can I do for you?");
                player.getInterfaceState().setNextDialogueId(0, 42);
                break;
            case 42:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Do you have any supplies for me?",
                        "Nothing, thanks.");
                player.getInterfaceState().setNextDialogueId(0, 43);
                player.getInterfaceState().setNextDialogueId(1, 34);
                break;
            case 43:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Do you have any supplies for me?");
                player.getInterfaceState().setNextDialogueId(0, 44);
                break;
            case 44:
                if (player.hasReceivedStarterRunes()) {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "I've already given you your set of starter runes.");
                } else {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "Sure thing. Here's enough rune stones for 50 casts",
                            "of Wind Strike. Use them wisely as I won't be",
                            "replacing them for you.");
                    player.setReceivedStarterRunes(true);
                    player.getInterfaceState().setNextDialogueId(0, 45);
                    player.getInventory().add(new Item(MagicCombatAction.AIR_RUNE, 50));
                    player.getInventory().add(new Item(MagicCombatAction.MIND_RUNE, 50));
                }
                break;
            case 45:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Thanks!");
                break;
            case 46:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hey.");
                player.getInterfaceState().setNextDialogueId(0, 47);
                break;
            case 47:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "'Ello there! Would ya' like to buy some of me own",
                        "knitwear? It's all the rage with the young folk. For",
                        "no more than 750 coins, now I can't say fairer",
                        "than that.");
                player.getInterfaceState().setNextDialogueId(0, 48);
                break;
            case 48:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 49);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 49:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Sure.");
                player.getInterfaceState().setNextDialogueId(0, 50);
                break;
            case 50:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 0, 1);
                break;
            case 51:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "No thank you.");
                break;
            case 52:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I see you have 99 defence, well done! Would you",
                        "like to buy the defence cape? It'll be 99,0000",
                        "coins.");
                player.getInterfaceState().setNextDialogueId(0, 53);
                break;
            case 53:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "I have 99,000 coins, here you go.",
                        "99,000 coins, that's outrageous.");
                player.getInterfaceState().setNextDialogueId(0, 54);
                player.getInterfaceState().setNextDialogueId(1, 56);
                break;
            case 54:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "I have 99,000 coins, here you go.");
                player.getInterfaceState().setNextDialogueId(0, 55);
                break;
            case 55:
                if (player.getInventory().getCount(995) >= 99000) {
                    skillcape = player.checkForSkillcape(new Item(9753));
                    hood = new Item(9755);
                    if (player.getInventory().hasRoomFor(skillcape) && player.getInventory().hasRoomFor(skillcape)) {
                        player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                                "Here you go.");
                        player.getInventory().remove(new Item(995, 99000));
                        player.getInventory().add(hood);
                        player.getInventory().add(skillcape);
                    } else {
                        player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                                "Perhaps you should clear some space from",
                                "your inventory first.");
                    }
                } else {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "You don't have 99,000 coins.");
                }
                break;
            case 56:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "99,000 coins, that's outrageous.");
                break;
            case 57:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello, how may I help you?");
                player.getInterfaceState().setNextDialogueId(0, 58);
                break;
            case 58:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What are you selling?",
                        "Goodbye.");
                player.getInterfaceState().setNextDialogueId(0, 59);
                player.getInterfaceState().setNextDialogueId(1, 28);
                break;
            case 59:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "What are you selling?");
                player.getInterfaceState().setNextDialogueId(0, 60);
                break;
            case 60:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 0, 1);
                break;
            case 61:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello there, would you like to sail anywhere? It's free",
                        "of charge for Jatizso, but any other exotic lands you",
                        "want sailing to might cost a fair bit.");
                player.getInterfaceState().setNextDialogueId(0, 62);
                break;
            case 62:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Jatizso.",
                        "Nowhere.");
                player.getInterfaceState().setNextDialogueId(0, 63);
                player.getInterfaceState().setNextDialogueId(1, 66);
                break;
            case 63:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Jatizso please.");
                player.getInterfaceState().setNextDialogueId(0, 64);
                break;
            case 64:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Land-ho!");
                player.getInterfaceState().setNextDialogueId(0, 65);
                break;
            case 65:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(2421, 3781, 0));
                break;
            case 66:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Nowhere, thanks for your time.");
                break;
            case 67:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hi there. I'm offering a boating service with my",
                        "husband. Would you like to go anywhere? It will cost",
                        "you unless it's Neitiznot.");
                player.getInterfaceState().setNextDialogueId(0, 68);
                break;
            case 68:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Neitiznot.",
                        "Nowhere.");
                player.getInterfaceState().setNextDialogueId(0, 69);
                player.getInterfaceState().setNextDialogueId(1, 66);
                break;
            case 69:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Neitiznot please.");
                player.getInterfaceState().setNextDialogueId(0, 70);
                break;
            case 70:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Land-ho!");
                player.getInterfaceState().setNextDialogueId(0, 71);
                break;
            case 71:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(2311, 3780, 0));
                break;
            case 72:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello. Would you like to buy some of my fine cuisine?");
                player.getInterfaceState().setNextDialogueId(0, 73);
                break;
            case 73:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What are you selling?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 74);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 74:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "What are you selling?");
                player.getInterfaceState().setNextDialogueId(0, 75);
                break;
            case 75:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 2, 1);
                break;
            case 76:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.ATTACK_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "  Congratulations, you have just advanced an Attack level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.ATTACK) + ".");
                break;
            case 77:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.DEFENCE_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        " Congratulations, you have just advanced a Defence level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.DEFENCE) + ".");
                if (player.getSkills().getLevelForExperience(Skills.DEFENCE) > 98) {
                    player.getInterfaceState().setNextDialogueId(0, 107);
                }
                break;
            case 78:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.STRENGTH_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Strength level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.STRENGTH) + ".");
                break;
            case 79:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.HITPOINT_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Hitpoints level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.HITPOINTS) + ".");
                break;
            case 80:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.RANGING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Ranged level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.RANGE) + ".");
                if (player.getSkills().getLevelForExperience(Skills.RANGE) > 98) {
                    player.getInterfaceState().setNextDialogueId(0, 108);
                }
                break;
            case 81:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PRAYER_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Prayer level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.PRAYER) + ".");
                break;
            case 82:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.MAGIC_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Magic level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.MAGIC) + ".");
                break;
            case 83:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.COOKING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Cooking level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.COOKING) + ".");
                break;
            case 84:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.WOODCUTTING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Woodcutting level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.WOODCUTTING) + ".");
                break;
            case 85:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.FLETCHING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "  Congratulations, you have just advanced a Fletching level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.FLETCHING) + ".");
                break;
            case 86:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.FISHING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Fishing level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.FISHING) + ".");
                break;
            case 87:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.FIREMAKING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Firemaking level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.FIREMAKING) + ".");
                break;
            case 88:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.CRAFTING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Crafting level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.CRAFTING) + ".");
                break;
            case 89:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.SMITHING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "  Congratulations, you have just advanced a Smithing level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.SMITHING) + ".");
                break;
            case 90:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.MINING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "  Congratulations, you have just advanced a Mining level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.MINING) + ".");
                break;
            case 91:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.HERBLORE_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Herblore level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.HERBLORE) + ".");
                break;
            case 92:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.AGILITY_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced an Agility level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.AGILITY) + ".");
                break;
            case 93:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.THIEVING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Thieving level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.THIEVING) + ".");
                break;
            case 94:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.SLAYER_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Slayer level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.SLAYER) + ".");
                break;
            case 95:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.FARMING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Farming level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.FARMING) + ".");
                break;
            case 96:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.RUNECRAFTING_LEVEL_UP, -1, FacialAnimation.DEFAULT,
                        "Congratulations, you have just advanced a Runecrafting level!",
                        "You have now reached level " + player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) + ".");
                break;
            case 99:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello. How may I help you?");
                player.getInterfaceState().setNextDialogueId(0, 100);
                break;
            case 100:
                if (player.getSkills().getLevelForExperience(Skills.RANGE) > 98) {
                    player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                            "What are you selling?",
                            "Talk about range skillcape.",
                            "Goodbye.");
                    player.getInterfaceState().setNextDialogueId(0, 101);
                    player.getInterfaceState().setNextDialogueId(1, 103);
                    player.getInterfaceState().setNextDialogueId(2, 29);
                } else {
                    player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                            "What are you selling?",
                            "Goodbye.");
                    player.getInterfaceState().setNextDialogueId(0, 101);
                    player.getInterfaceState().setNextDialogueId(1, 29);
                }
                break;
            case 101:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "What are you selling?");
                player.getInterfaceState().setNextDialogueId(0, 102);
                break;
            case 102:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 3, 1);
                break;
            case 103:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Ah, I see you have maxed the ranging skill! Bravo!",
                        "I have something special for elite rangers such as",
                        "yourself, would you like to buy it? It'll be",
                        "99,000 coins.");
                player.getInterfaceState().setNextDialogueId(0, 104);
                break;
            case 104:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure, here's 99,000 coins.",
                        "No way.");
                player.getInterfaceState().setNextDialogueId(0, 105);
                player.getInterfaceState().setNextDialogueId(1, 106);
                break;
            case 105:
                if (player.getInventory().getCount(995) >= 99000) {
                    skillcape = player.checkForSkillcape(new Item(9756));
                    hood = new Item(9758);
                    if (player.getInventory().hasRoomFor(skillcape) && player.getInventory().hasRoomFor(skillcape)) {
                        player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                                "Here you go.");
                        player.getInventory().remove(new Item(995, 99000));
                        player.getInventory().add(hood);
                        player.getInventory().add(skillcape);
                    } else {
                        player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                                "Perhaps you should clear some space from",
                                "your inventory first.");
                    }
                } else {
                    player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                            "You don't have 99,000 coins.");
                }
                break;
            case 106:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "No way.");
                break;
            case 107:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 9753, FacialAnimation.DEFAULT,
                        "Congratulations! You are now a master of Defence. Why not visit the Melee combat tutor on Neitiznot? He has something special that is only available to the true masters of the Defence skill!");
                break;
            case 108:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.MESSAGE_MODEL_LEFT, 9756, FacialAnimation.DEFAULT,
                        "Congratulations! You are now a master of Ranging. Why not visit Lowe in the northern building on Neitiznot? He has something special that is only available to the true masters of the Ranging skill!");
                break;
            case 109:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello there, would you like to sample my fine ores?");
                player.getInterfaceState().setNextDialogueId(0, 110);
                break;
            case 110:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 111);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 111:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Sure.");
                player.getInterfaceState().setNextDialogueId(0, 112);
                break;
            case 112:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 4, 1);
                break;
            case 113:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Interested in me bars are ya'?");
                player.getInterfaceState().setNextDialogueId(0, 114);
                break;
            case 114:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Totally.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 115);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 115:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Totally.");
                player.getInterfaceState().setNextDialogueId(0, 116);
                break;
            case 116:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 5, 1);
                break;
            case 117:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Would you like to see some of my hand",
                        "crafted items?");
                player.getInterfaceState().setNextDialogueId(0, 118);
                break;
            case 118:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 119);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 119:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Yes.");
                player.getInterfaceState().setNextDialogueId(0, 120);
                break;
            case 120:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 6, 1);
                break;
            case 121:
                player.getActionSender().sendDialogue("Squire", DialogueType.NPC, 606, FacialAnimation.DEFAULT,
                        "Congratulations, " + player.getName() + "! You have just completed",
                        "the White Knight training course. Now it is time for you",
                        "to venture out into our wide world, and defend it as an",
                        "honourable White Knight.");
                player.getInterfaceState().setNextDialogueId(0, 122);
                break;
            case 122:
                player.getActionSender().sendDialogue("Squire", DialogueType.NPC, 606, FacialAnimation.DEFAULT,
                        "Here is your complimentary starter pack.");
                player.getInterfaceState().setNextDialogueId(0, 123);
                break;
            case 123:
                if (player.getAttribute("squire") != null) {
                    World.getWorld().unregister((NPC) player.getAttribute("squire"));
                    Location teleport = Location.create(2965, 3365, 0);
                    player.setLocation(teleport);
                    player.setTeleportTarget(teleport);
                    player.removeAllAttributes();
                    player.getActionSender().removeAllInterfaces().sendDefaultChatbox().sendSidebarInterfaces();
                }
                break;
            case 124:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello there! Up for a quick trim or a totally",
                        "new look?");
                player.getInterfaceState().setNextDialogueId(0, 125);
                break;
            case 125:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Haircut.",
                        "Shave.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 126);
                player.getInterfaceState().setNextDialogueId(1, 129);
                player.getInterfaceState().setNextDialogueId(2, 51);
                break;
            case 126:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "I want a haircut please.");
                player.getInterfaceState().setNextDialogueId(0, 127);
                break;
            case 127:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Certainly.");
                player.getInterfaceState().setNextDialogueId(0, 128);
                break;
            case 128:
                player.getActionSender().removeChatboxInterface().sendConfig(261, 1).sendConfig(262, 1).sendInterface(204 - player.getAppearance().getGender(), true);
                player.setInterfaceAttribute("newHair", 0 + (player.getAppearance().getGender() * 45));
                player.setInterfaceAttribute("newHairColour", 0);
                break;
            case 129:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "I want a shave please.");
                player.getInterfaceState().setNextDialogueId(0, 131 - player.getAppearance().getGender());
                break;
            case 130:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I'm sorry but women don't grow facial hair.");
                break;
            case 131:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Certainly.");
                player.getInterfaceState().setNextDialogueId(0, 132);
                break;
            case 132:
                player.getActionSender().removeChatboxInterface().sendConfig(261, 1).sendConfig(262, 1).sendInterface(199, true);
                player.setInterfaceAttribute("newBeard", 11);
                player.setInterfaceAttribute("newHairColour", 0);
                break;
            case 133:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "*gulp* ... Hiya. Fancy changing your body for a",
                        "small fee of 3,000 coins? It will reset all your",
                        "clothes to their default, so bear that in mind!");
                player.getInterfaceState().setNextDialogueId(0, 134);
                break;
            case 134:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 135);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 135:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Sure.");
                player.getInterfaceState().setNextDialogueId(0, 136);
                break;
            case 136:
                player.getActionSender().removeChatboxInterface().sendConfig(261, 1).sendConfig(262, 1).sendInterface(205, true);
                player.setInterfaceAttribute("newGender", 0);
                player.setInterfaceAttribute("newSkinColour", 0);
                break;
            case 137:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hi there. Fancy a drink?");
                player.getInterfaceState().setNextDialogueId(0, 138);
                break;
            case 138:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 139);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 139:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 3, 1);
                break;
            case 140:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "My shields are amazing. Would you like to see",
                        "the best ones I have?");
                player.getInterfaceState().setNextDialogueId(0, 141);
                break;
            case 141:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 142);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 142:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 4, 1);
                break;
            case 143:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I'm the best smith in this town. Cassie is just jealous.",
                        "I'll prove it, my maces are the finest fashion work",
                        "around! Would you like to see them?");
                player.getInterfaceState().setNextDialogueId(0, 144);
                break;
            case 144:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 145);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 145:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 5, 1);
                break;
            case 146:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hi there. Are you interested in buying some good",
                        "quality chainmail?");
                player.getInterfaceState().setNextDialogueId(0, 147);
                break;
            case 147:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 148);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 148:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 6, 1);
                break;
            case 149:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "'ay there tall stuff. Do you be needing some",
                        "of the best pickaxes?");
                player.getInterfaceState().setNextDialogueId(0, 150);
                break;
            case 150:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 151);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 151:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 7, 1);
                break;
            case 152:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Fancy takin' a look at me ore store?");
                player.getInterfaceState().setNextDialogueId(0, 153);
                break;
            case 153:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 154);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 154:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 8, 1);
                break;
            case 155:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Heya. Do you need any runes or mage supplies?");
                player.getInterfaceState().setNextDialogueId(0, 156);
                break;
            case 156:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 157);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 157:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 9, 1);
                break;
            case 158:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "'Ay lad, you be talkin' to an expert on axes. Don't",
                        "suppose you be needin anythin' of the sort?");
                player.getInterfaceState().setNextDialogueId(0, 159);
                break;
            case 159:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 160);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 160:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 10, 1);
                break;
            case 161:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Ello ello ello! Need any fishin' gear matey?");
                player.getInterfaceState().setNextDialogueId(0, 162);
                break;
            case 162:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 163);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 163:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 11, 1);
                break;
            case 164:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello there. Can I interest you in the best",
                        "baked food in the land?");
                player.getInterfaceState().setNextDialogueId(0, 165);
                break;
            case 165:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 166);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 166:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 12, 1);
                break;
            case 167:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hi. Would you like me to sail you anywhere?");
                player.getInterfaceState().setNextDialogueId(0, 168);
                break;
            case 168:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Rellekka please.",
                        "Karamja please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 169);
                player.getInterfaceState().setNextDialogueId(1, 194);
                player.getInterfaceState().setNextDialogueId(2, 51);
                break;
            case 169:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Rellekka please.");
                player.getInterfaceState().setNextDialogueId(0, 170);
                break;
            case 170:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(2629, 3693, 0));
                break;
            case 171:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hi. Would you like me to sail you anywhere?");
                player.getInterfaceState().setNextDialogueId(0, 172);
                break;
            case 172:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Port Sarim please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 196);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 173:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(3029, 3217, 0));
                break;
            case 174:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I only offer my goods to the best warriors,",
                        "and you are by far one of the best I've seen.",
                        "Do you need any battle gear brother?");
                player.getInterfaceState().setNextDialogueId(0, 175);
                break;
            case 175:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 176);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 176:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 13, 1);
                break;
            case 177:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Step right up I got the best fish in all of Rellekka!");
                player.getInterfaceState().setNextDialogueId(0, 178);
                break;
            case 178:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 179);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 179:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 14, 1);
                break;
            case 180:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Wow, you beat my trial! Talk to me ",
                        "anytime you need some Fremennik gear.");
                break;
            case 181:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I only offer my goods to the best warriors.");
                player.getInterfaceState().setNextDialogueId(0, 182);
                break;
            case 182:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "How do I prove I'm the best?",
                        "Bye then.");
                player.getInterfaceState().setNextDialogueId(0, 184);
                player.getInterfaceState().setNextDialogueId(1, 183);
                break;
            case 183:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Bye then.");
                break;
            case 184:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "How do I prove I'm the best?");
                player.getInterfaceState().setNextDialogueId(0, 185);
                break;
            case 185:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "You must take on my Fremennik Trial. When",
                        "you are ready for combat, enter down the ladder at",
                        "the back of my house. From there on your combat",
                        "skills will be severely tested.");
                player.getInterfaceState().setNextDialogueId(0, 186);
                break;
            case 186:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "What happens if I die?");
                player.getInterfaceState().setNextDialogueId(0, 187);
                break;
            case 187:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "I shall return you your items.");
                break;
            case 188:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Would you like to buy some vintage",
                        "Fremennik clothes?");
                player.getInterfaceState().setNextDialogueId(0, 189);
                break;
            case 189:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 190);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 190:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 15, 1);
                break;
            case 191:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "In need of the finest metal work in the land, mate?");
                player.getInterfaceState().setNextDialogueId(0, 189);
                break;
            case 192:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 193);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 193:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 16, 1);
                break;
            case 194:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Karamja please.");
                player.getInterfaceState().setNextDialogueId(0, 195);
                break;
            case 195:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(2956, 3146, 0));
                break;
            case 196:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Port Sarim please.");
                player.getInterfaceState().setNextDialogueId(0, 173);
                break;
            case 197:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Banana's are a great source of potassium!");
                player.getInterfaceState().setNextDialogueId(0, 198);
                break;
            case 198:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Tell me more!",
                        "Good bye.");
                player.getInterfaceState().setNextDialogueId(0, 200);
                player.getInterfaceState().setNextDialogueId(1, 199);
                break;
            case 199:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Good bye.");
                break;
            case 200:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 17, 1);
                break;
            case 201:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "*hic* my beers are great!!!");
                player.getInterfaceState().setNextDialogueId(0, 202);
                break;
            case 202:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 203);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 203:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 18, 1);
                break;
            case 204:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "Hello JalYt-Ket-" + player.getName() + ". You want",
                        "any equipment? I sell for good price.");
                player.getInterfaceState().setNextDialogueId(0, 205);
                break;
            case 205:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 206);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 206:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 19, 1);
                break;
            case 207:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "What you want JalYt-Ket-" + player.getName() + "?");
                player.getInterfaceState().setNextDialogueId(0, 208);
                break;
            case 208:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "What have you got?",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 209);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 209:
                player.getActionSender().removeChatboxInterface();
                Shop.open(player, 20, 1);
                break;
            case 210:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getDefinition().getId(), FacialAnimation.DEFAULT,
                        "You want to access your bank account,",
                        "JalYt-Ket-" + player.getName() + "?");
                player.getInterfaceState().setNextDialogueId(0, 211);
                break;
            case 211:
                player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Yes please.",
                        "No thank you.");
                player.getInterfaceState().setNextDialogueId(0, 2);
                player.getInterfaceState().setNextDialogueId(1, 51);
                break;
            case 212:
                player.getActionSender().sendDialogue("TzHaar-Mej-Kah", DialogueType.NPC, 2618, FacialAnimation.DEFAULT,
                        "Wait for my signal before fighting.");
                break;
            case 213:
                player.getActionSender().sendDialogue("TzHaar-Mej-Kah", DialogueType.NPC, 2618, FacialAnimation.DEFAULT,
                        "FIGHT!");
                break;
            case 214:
                player.getActionSender().sendDialogue("Subject?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Quest",
                        "Conversation",
                        "Cancel");
                player.getInterfaceState().setNextDialogueId(0, 215);
                player.getInterfaceState().setNextDialogueId(1, 216);
                player.getInterfaceState().setNextDialogueId(2, 97);
                break;
            case 215:
                if (!new DwarfCannonQuest().hasRequirements(player)) {
                    player.getActionSender().removeChatboxInterface();
                    player.getActionSender().sendMessage("You don't have the requirements.");
                    return;
                }
                new DwarfCannonQuest().start(player);
                new DwarfCannonQuest().getDialogues(player, npc.getDefinition().getId(), 0);
                return;
            //break;
            case 216:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
                        "Hi.");
                player.getInterfaceState().setNextDialogueId(0, 97);
                break;
            case 217:
                player.getActionSender().sendDialogue("Monk of Entrana", DialogueType.NPC, 657, FacialAnimation.HAPPY,
                        "Would you like to visit Entrana?",
                        "You can bring anything you want..");
                player.getInterfaceState().setNextDialogueId(0, 218);
                break;
            case 218:
                player.getActionSender().sendDialogue("Visit Entrana?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure",
                        "No thanks");
                player.getInterfaceState().setNextDialogueId(0, 219);
                player.getInterfaceState().setNextDialogueId(1, 97);
                break;
            case 219:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
                        "Sure");
                player.getInterfaceState().setNextDialogueId(0, 220);
                break;
            case 220:
                player.getActionSender().removeChatboxInterface();
                if (player.getQuestStorage().getQuestStage(new WerewolfQuest()) == 1) {
                    player.getQuestStorage().setQuestStage(new WerewolfQuest(), 2);
                    new WerewolfQuest().getDialogues(player, 5896, 0);
                    player.setTeleportTarget(Location.create(2834, 3335, player.getIndex() * 4));
                    return;
                }
                player.setTeleportTarget(Location.create(2834, 3335, 0));
                break;
            case 221:
                player.getActionSender().sendDialogue("Head back?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure",
                        "No thanks");
                player.getInterfaceState().setNextDialogueId(0, 222);
                player.getInterfaceState().setNextDialogueId(1, 97);
                break;
            case 222:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
                        "Sure");
                player.getInterfaceState().setNextDialogueId(0, 223);
                break;
            case 223:
                player.getActionSender().removeChatboxInterface();
                player.setTeleportTarget(Location.create(3048, 3235, 0));
                break;
            case 224:
                player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
                        "Hey there...", "You alright?");
                player.getInterfaceState().setNextDialogueId(0, 225);
                break;
            case 225:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 5893, FacialAnimation.SAD,
                        "Can you help me fight a werewolf..?");
                player.getInterfaceState().setNextDialogueId(0, 226);
                break;
            case 226:
                player.getActionSender().sendDialogue("Help fight a wolf?", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
                        "Sure!",
                        "No thanks..");
                player.getInterfaceState().setNextDialogueId(0, 227);
                player.getInterfaceState().setNextDialogueId(1, 97);
                break;
            case 227:
                if (!new WerewolfQuest().hasRequirements(player)) {
                    player.getActionSender().removeChatboxInterface();
                    player.getActionSender().sendMessage("You don't have the requirements.");
                    return;
                }
                player.getActionSender().removeChatboxInterface();
                new WerewolfQuest().start(player);
                new WerewolfQuest().getDialogues(player, npc.getDefinition().getId(), 0);
                break;
            case 228:
                player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 5893, FacialAnimation.DRUNK_TO_LEFT,
                        "I'm a hero!");
                player.getInterfaceState().setNextDialogueId(0, 97);
                break;
            case 97:
            case 98:
            default:
                player.getActionSender().removeChatboxInterface();
                break;
        }
    }

    public static void advanceDialogue(Player player, int index) {
        int dialogueId = player.getInterfaceState().getNextDialogueId(index);
        if (dialogueId == -1) {
            player.getActionSender().removeChatboxInterface();
            return;
        }
        openDialogue(player, dialogueId);
    }

}