importPackage(org.rs2server.rs2.model)

importPackage(org.rs2server.rs2.model.quests.impl)

function talkTo956(player, npc) {
    if (player.getQuestStorage().hasFinished(new DwarfCannonQuest())) {
        DialogueManager.openDialogue(player, 216);
        return;
    }
    if (player.getQuestStorage().hasStarted(new DwarfCannonQuest())) {
        if (player.getQuestStorage().getQuestStage(new DwarfCannonQuest()) == 3) {
            new DwarfCannonQuest().getDialogues(player, npc.getDefinition().getId(), 4);
            return;
        }
        new DwarfCannonQuest().getDialogues(player, npc.getDefinition().getId(), 0);
        return;
    } else {
        DialogueManager.openDialogue(player, 214);
    }

}

function talkTo209(player, npc) {
    if (player.getQuestStorage().getQuestStage(new DwarfCannonQuest()) == 2) {
        new DwarfCannonQuest().getDialogues(player, 209, 0);
    }
}

function talkTo5893(player, npc) {
    if (player.getQuestStorage().hasFinished(new WerewolfQuest())) {
        DialogueManager.openDialogue(player, 228);
        return;
    }
    if (player.getQuestStorage().hasStarted(new WerewolfQuest())) {
        new WerewolfQuest().getDialogues(player, 5893, 0);
    } else {
        DialogueManager.openDialogue(player, 224);
    }

}

function talkTo5896(player, npc) {
    if (player.getQuestStorage().getQuestStage(new WerewolfQuest()) == 2) {
        new WerewolfQuest().getDialogues(player, 5896, 1);
    } else {
        //  DialogueManager.openDialogue(player, 214);
    }

}

function talkTo657(player, npc) {
    DialogueManager.openDialogue(player, 217);
}

function talkTo658(player, npc) {
    DialogueManager.openDialogue(player, 221);
}

function talkTo598(player, npc) {
    DialogueManager.openDialogue(player, 124);
}

function tradeWith598(player, npc) {
    DialogueManager.openDialogue(player, 126);
}

function talkTo599(player, npc) {
    DialogueManager.openDialogue(player, 133);
}

function talkTo520(player, npc) {
    DialogueManager.openDialogue(player, 57);
}

function tradeWith520(player, npc) {
    Shop.open(player, 0, 1);
}

function talkTo736(player, npc) {
    DialogueManager.openDialogue(player, 137);
}

function talkTo577(player, npc) {
    DialogueManager.openDialogue(player, 140);
}

function tradeWith577(player, npc) {
    Shop.open(player, 4, 1);
}

function talkTo580(player, npc) {
    DialogueManager.openDialogue(player, 143);
}

function tradeWith580(player, npc) {
    Shop.open(player, 5, 1);
}

function talkTo581(player, npc) {
    DialogueManager.openDialogue(player, 146);
}

function tradeWith581(player, npc) {
    Shop.open(player, 6, 1);
}

function talkTo594(player, npc) {
    DialogueManager.openDialogue(player, 149);
}

function tradeWith594(player, npc) {
    Shop.open(player, 7, 1);
}

function talkTo579(player, npc) {
    DialogueManager.openDialogue(player, 152);
}

function tradeWith579(player, npc) {
    Shop.open(player, 8, 1);
}

function talkTo583(player, npc) {
    DialogueManager.openDialogue(player, 155);
}

function tradeWith583(player, npc) {
    Shop.open(player, 9, 1);
}

function talkTo559(player, npc) {
    DialogueManager.openDialogue(player, 158);
}

function tradeWith559(player, npc) {
    Shop.open(player, 10, 1);
}

function talkTo558(player, npc) {
    DialogueManager.openDialogue(player, 161);
}

function tradeWith558(player, npc) {
    Shop.open(player, 11, 1);
}

function talkTo557(player, npc) {
    DialogueManager.openDialogue(player, 164);
}

function tradeWith557(player, npc) {
    Shop.open(player, 12, 1);
}

function talkTo376(player, npc) {
    DialogueManager.openDialogue(player, 167);
}

function tradeWith376(player, npc) {
    DialogueManager.openDialogue(player, 168);
}

function talkTo377(player, npc) {
    DialogueManager.openDialogue(player, 171);
}

function tradeWith377(player, npc) {
    DialogueManager.openDialogue(player, 172);
}

function talkTo380(player, npc) {
    DialogueManager.openDialogue(player, 171);
}

function tradeWith380(player, npc) {
    DialogueManager.openDialogue(player, 172);
}

function talkTo1303(player, npc) {
    if (player.completedFremennikTrials()) {
        DialogueManager.openDialogue(player, 174);
    } else {
        DialogueManager.openDialogue(player, 181);
    }
}

function tradeWith1303(player, npc) {
    if (player.completedFremennikTrials()) {
        Shop.open(player, 13, 1);
    } else {
        player.getActionSender().sendMessage("You must complete the Fremennik Trials minigame before accessing this shop.");
    }
}

function talkTo1369(player, npc) {
    DialogueManager.openDialogue(player, 177);
}

function tradeWith1369(player, npc) {
    Shop.open(player, 14, 1);
}

function talkTo1301(player, npc) {
    DialogueManager.openDialogue(player, 188);
}

function tradeWith1301(player, npc) {
    Shop.open(player, 15, 1);
}

function talkTo549(player, npc) {
    DialogueManager.openDialogue(player, 191);
}

function tradeWith549(player, npc) {
    Shop.open(player, 16, 1);
}

function talkTo379(player, npc) {
    DialogueManager.openDialogue(player, 197);
}

function talkTo568(player, npc) {
    DialogueManager.openDialogue(player, 201);
}

function tradeWith568(player, npc) {
    Shop.open(player, 18, 1);
}

function talkTo2620(player, npc) {
    DialogueManager.openDialogue(player, 204);
}

function tradeWith2620(player, npc) {
    Shop.open(player, 19, 1);
}

function talkTo2623(player, npc) {
    DialogueManager.openDialogue(player, 207);
}

function tradeWith2623(player, npc) {
    Shop.open(player, 20, 1);
}

function talkTo2619(player, npc) {
    DialogueManager.openDialogue(player, 210);
}

function tradeWith209(player, npc) {
    if (player.getQuestStorage().hasFinished(new DwarfCannonQuest())) {
        Shop.open(player, 21, 1);
    }
}