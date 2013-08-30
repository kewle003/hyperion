package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.model.quests.QuestRepository;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;


/**
 * The death tickable handles player and npc deaths. Drops loot, does animation,
 * teleportation, etc.
 *
 * @author Graham
 * @author Scu11
 */
public class DeathTick extends Tickable {

    /**
     * The mob who has just died.
     */
    private Mob mob;

    /**
     * The random number generator.
     */
    private final Random random = new Random();

    /**
     * Creates the death event for the specified entity.
     *
     * @param mob The mob whose death has just happened.
     */
    public DeathTick(Mob mob, int ticks) {
        super(ticks);
        this.mob = mob;
    }

    @Override
    public void execute() {
        this.stop();
        if (mob.getCombatState().isDead()) { //they might of been saved in this period
            /*
                * If set to true, the minigame handles items such as teleporting.
                */
            boolean minigameDeathHook = false;

            /**
             * Quest death hooks.
             */
            boolean questDeathHook = false;

            /*
                * The killer of this mob.
                */
            Mob killer = (mob.getCombatState().getDamageMap().highestDamage() != null && !mob.isDestroyed()) ? mob.getCombatState().getDamageMap().highestDamage() : mob;

            /*
                * Drops the loot and performs minigame kill hooks.
                */
            if (killer.isPlayer()) {
                Player player = (Player) killer;
                mob.dropLoot(player);
                if (player.getMinigame() != null) {
                    player.getMinigame().killHook(player, mob);
                }
                for (Quest quest : QuestRepository.QUEST_DATABASE.values()) {
                    if (quest != null) {
                        quest.killHook(player, mob);
                    }
                }
            } else {
                mob.dropLoot(mob);
            }

            /*
                * The location to teleport to.
                */
            Location teleportTo = Mob.DEFAULT_LOCATION;

            /*
                * Resets the opponents tag timer. Player only as NPC's reset their killer as soon as they die.
                */
            if (!mob.isNPC() && mob.getCombatState().getLastHitBy() != null && mob.getCombatState().getLastHitBy().getCombatState().getLastHitTimer() > (System.currentTimeMillis() + 4000) && mob.getCombatState().getLastHitBy().getCombatState().getLastHitBy() == mob) {
                mob.getCombatState().getLastHitBy().getCombatState().setLastHitBy(null);
                mob.getCombatState().getLastHitBy().getCombatState().setLastHitTimer(0);
            }
            /*
                * Resets our tag timer.
                */
            mob.getCombatState().setLastHitBy(null);
            mob.getCombatState().setLastHitTimer(0);

            /*
                * Resets various attributes.
                */
            mob.getCombatState().setDead(false);
            mob.resetVariousInformation();

            /*
                * Performs checks for players/npcs.
                */
            if (mob.isPlayer()) {
                final Player player = (Player) mob;
                player.getActionSender().updateSpecialConfig();
                player.getActionSender().sendMessage("Oh dear, you are dead!");
                player.getActionSender().sendBonuses();
                player.getActionSender().updateRunningConfig();
                if (player.getMinigame() != null) {
                    minigameDeathHook = player.getMinigame().deathHook(player);
                }
                for (Quest quest : QuestRepository.QUEST_DATABASE.values()) {
                    if (quest != null) {
                        questDeathHook = quest.deathHook(player);
                    }
                }
            } else if (mob.isNPC()) {
                final NPC npc = (NPC) mob;
                if (npc.getCombatDefinition().getRespawnTicks() > 0) {
                    teleportTo = Location.create(1, 1, 0);
                    World.getWorld().submit(new Tickable(npc.getCombatDefinition().getRespawnTicks()) {
                        public void execute() {
                            npc.setTeleportTarget(npc.getSpawnLocation());
                            npc.setLocation(npc.getSpawnLocation());
                            npc.setDirection(npc.getSpawnDirection());
                            this.stop();
                        }
                    });
                } else {
                    teleportTo = null;
                    World.getWorld().unregister(npc);
                }
            }

            /*
                * Teleports the mob if the minigame hasn't handled it.
                */
            if (!minigameDeathHook && teleportTo != null ||
                    !questDeathHook && teleportTo != null) {
                mob.setTeleportTarget(teleportTo);
                mob.getCombatState().resetBonuses();
                mob.setDefaultAnimations();
            }

            /*
                * If in a PvP situation, send the defeat message.
                */
            if (killer.isPlayer() && mob.isPlayer() && killer != mob) {
                switch (random.nextInt(10)) {
                    default:
                    case 0:
                        killer.getActionSender().sendMessage("You have defeated " + mob.getUndefinedName() + ".");
                        break;
                    case 1:
                        killer.getActionSender().sendMessage("Can anyone defeat you? Certainly not " + mob.getUndefinedName() + ".");
                        break;
                    case 2:
                        killer.getActionSender().sendMessage(mob.getUndefinedName() + " falls before your might.");
                        break;
                    case 3:
                        killer.getActionSender().sendMessage("A humiliating defeat for " + mob.getUndefinedName() + ".");
                        break;
                    case 4:
                        killer.getActionSender().sendMessage("You were clearly a better fighter than " + mob.getUndefinedName() + ".");
                        break;
                    case 5:
                        killer.getActionSender().sendMessage(mob.getUndefinedName() + " has won a free ticket to Lumbridge.");
                        break;
                    case 6:
                        killer.getActionSender().sendMessage("It's all over for " + mob.getUndefinedName() + ".");
                        break;
                    case 7:
                        killer.getActionSender().sendMessage("With a crushing blow you finish " + mob.getUndefinedName() + ".");
                        break;
                    case 8:
                        killer.getActionSender().sendMessage(mob.getUndefinedName() + " regrets the day they met you in combat.");
                        break;
                    case 9:
                        killer.getActionSender().sendMessage(mob.getUndefinedName() + " didn't stand a chance against you.");
                        break;
                }
            }

            /*
                * Resets the mobs animation and frozen flag.
                */
            World.getWorld().submit(new Tickable(1) {
                public void execute() {
                    mob.playAnimation(Animation.create(-1));
                    mob.getCombatState().setCanMove(true);
                    this.stop();
                }
            });
        }
    }

}