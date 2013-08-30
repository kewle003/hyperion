package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.util.Misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Thieving class.
 *
 * @author Fire cape
 */
public class Thieving extends HarvestingAction {

    /**
     * The stall we are mining.
     */
    private GameObject object;

    /**
     * The stall we are mining.
     */
    private Stall stall;

    public Thieving(Mob mob, GameObject object) {
        super(mob);
        this.object = object;
        this.stall = Stall.forId(object.getId());
    }

    /**
     * Represents types of rocks.
     *
     * @author Michael
     */
    public static enum Stall {

        /**
         * Clay ore.
         */
        FISH_STALL(1, 50.20, 20, 13, new Item[]{new Item(361), new Item(359)}, new int[]{4277}, new int[]{4276});

        /**
         * The stall objects.
         */
        private int[] objects;

        /**
         * The level required..
         */
        private int level;

        /**
         * The experience granted for mining this stall.
         */
        private double experience;

        /**
         * The items rewarded.
         */
        private Item item[];

        /**
         * The time it takes for it to respawn.
         */
        private int respawnTimer;

        /**
         * How many times till it dies
         */
        private int maxHealth;


        /**
         * The stall to replace.
         */
        private int[] replacementObject;

        /**
         * A map of object ids to rocks.
         */
        private static Map<Integer, Stall> stall = new HashMap<Integer, Stall>();

        /**
         * Gets a stall by an object id.
         *
         * @param object The object id.
         * @return The stall, or <code>null</code> if the object is not a stall.
         */
        public static Stall forId(int object) {
            return stall.get(object);
        }

        static {
            for (Stall stalls : Stall.values()) {
                for (int object : stalls.objects) {
                    stall.put(object, stalls);
                }
            }
        }

        /**
         * Creates the tree.
         *
         * @param level      The required level.
         * @param experience The experience per steal.
         * @param objects    The object ids.
         */
        private Stall(int level, double experience, int respawnTimer, int maxHealth, Item[] items, int[] objects, int[] objectReplacement) {
            this.objects = objects;
            this.level = level;
            this.item = items;
            this.experience = experience;
            this.respawnTimer = respawnTimer;
            this.maxHealth = maxHealth;
            this.replacementObject = objectReplacement;
        }

        /**
         * @return the replacement stall
         */
        public int[] getReplacementStall() {
            return replacementObject;
        }

        /**
         * Gets the object ids.
         *
         * @return The object ids.
         */
        public int[] getObjectIds() {
            return objects;
        }

        /**
         * Gets the required level.
         *
         * @return The required level.
         */
        public int getRequiredLevel() {
            return level;
        }

        /**
         * The received items.
         *
         * @return The items
         */
        public Item[] getItems() {
            return item;
        }

        /**
         * Gets the experience.
         *
         * @return The experience.
         */
        public double getExperience() {
            return experience;
        }

        /**
         * @return the respawnTimer
         */
        public int getMaxHealth() {
            return maxHealth;
        }

        /**
         * @return the respawnTimer
         */
        public int getRespawnTimer() {
            return respawnTimer;
        }
    }

    @Override
    public AnimationPolicy getAnimationPolicy() {
        return AnimationPolicy.RESET_ALL;
    }

    @Override
    public CancelPolicy getCancelPolicy() {
        return CancelPolicy.ALWAYS;
    }

    @Override
    public StackPolicy getStackPolicy() {
        return StackPolicy.NEVER;
    }

    @Override
    public Animation getAnimation() {
        return Animation.STEAL_STALL;
    }

    @Override
    public int getCycleCount() {
        return 1;
    }

    @Override
    public double getExperience() {
        return stall.getExperience();
    }

    @Override
    public GameObject getGameObject() {
        return object;
    }
    @Override
    public int getGameObjectMaxHealth() {
        return stall.getMaxHealth();
    }

    @Override
    public String getHarvestStartedMessage() {
        return "You attempt to steal from the stall..";
    }

    @Override
    public String getLevelTooLowMessage() {
        return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + stall.getRequiredLevel() + " to mine this stall.";
    }

    @Override
    public int getObjectRespawnTimer() {
        return stall.getRespawnTimer();
    }

    @Override
    public GameObject getReplacementObject() {
        int index = 0;
        for (int i = 0; i < stall.getObjectIds().length; i++) {
            if (stall.getObjectIds()[i] == getGameObject().getDefinition().getId()) {
                index = i;
                break;
            }
        }
        return new GameObject(getGameObject().getLocation(), stall.getReplacementStall()[index], getGameObject().getType(), getGameObject().getDirection(), false);
    }

    @Override
    public int getRequiredLevel() {
        return stall.getRequiredLevel();
    }

    @Override
    public Item getReward() {
        return stall.getItems()[Misc.random(stall.getItems().length - 1)];
    }

    @Override
    public int getSkill() {
        return Skills.THIEVING;
    }

    @Override
    public String getSuccessfulHarvestMessage() {
        return "You manage to steal some " + getReward().getDefinition().getName()+ ".";
    }

    @Override
    public boolean canHarvest() {
        for (Stall stall : Stall.values()) {
            if (getMob().getSkills().getLevelForExperience(getSkill()) >= stall.getRequiredLevel()) {
                this.stall = stall;
                break;
            }
        }
        return true;
    }

    @Override
    public String getInventoryFullMessage() {
        return "Your inventory is too full to hold any more " + getReward().getDefinition().getName().toLowerCase().replaceAll(" ore", "") + ".";
    }
}