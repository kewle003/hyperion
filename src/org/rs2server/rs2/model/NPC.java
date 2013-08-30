package org.rs2server.rs2.model;

/*
 * IMPORTANT MESSAGE - READ BEFORE ADDING NEW METHODS/FIELDS TO THIS CLASS
 * 
 * Before you create a field (variable) or method in this class, which is specific to a particular
 * skill, quest, minigame, etc, THINK! There is almost always a better way (e.g. attribute system,
 * helper methods in other classes, etc.)
 * 
 * We don't want this to turn into another client.java! If you need advice on alternative methods,
 * feel free to discuss it with me.
 * 
 * Graham
 */

import java.util.Random;

import org.rs2server.rs2.GameEngine;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.Spell;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.task.Task;


/**
 * <p>Represents a non-player character in the in-game world.</p>
 * @author Graham Edgecombe
 *
 */
public class NPC extends Mob {
	
	/**
	 * The definition.
	 */
	private final NPCDefinition definition;
	
	/**
	 * The combat definition.
	 */
	private CombatNPCDefinition combatDefinition = null;
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * The npc's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The minimum coordinate for this npc.
	 */
	private Location minLocation;

	/**
	 * The maximum coordinate for this npc.
	 */
	private Location maxLocation;

	/**
	 * The spawn coordinate for this npc.
	 */
	private Location spawnLocation;
	
	/**
	 * The spawn direction for this npc.
	 */
	private int spawnDirection;

	/**
	 * The combat cooldown delay.
	 */
	private int combatCooldownDelay = 4;

	/**
	 * Creates the NPC with the specified definition.
	 * @param definition The definition.
	 */
	public NPC(NPCDefinition definition, Location spawnLocation, Location minLocation, Location maxLocation, int direction) {
		this.definition = definition;
		this.minLocation = minLocation;
		this.maxLocation = maxLocation;
		this.spawnLocation = spawnLocation;
		this.spawnDirection = direction;
		this.setDirection(direction);
	}
	
	/**
	 * Gets the NPC definition.
	 * @return The NPC definition.
	 */
	public NPCDefinition getDefinition() {
		return definition;
	}
	
	/**
	 * Gets the NPC combat definition.
	 * @return The NPC combat definition.
	 */
	public CombatNPCDefinition getCombatDefinition() {
		return combatDefinition;
	}

	/**
	 * @param combatDefinition the combatDefinition to setQuestStage
	 */
	public void setCombatDefinition(CombatNPCDefinition combatDefinition) {
		this.combatDefinition = combatDefinition;
	}

	/**
	 * @return the minLocation
	 */
	public Location getMinLocation() {
		return minLocation;
	}
	
	/**
	 * @return the maxLocation
	 */
	public Location getMaxLocation() {
		return maxLocation;
	}
	
	public boolean canMove() {
		return minLocation != null && maxLocation != null && !(minLocation == spawnLocation && maxLocation == spawnLocation);
	}
	
	/**
	 * @return the spawnLocation
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public int getSpawnDirection() {
		return spawnDirection;
	}

	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
		region.addMob(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
		region.removeMob(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex();
	}
	
	@Override
	public Skills getSkills() {
		return skills;
	}

	@Override
	public int getHeight() {
		return definition.getSize();
	}

	@Override
	public int getWidth() {
		return definition.getSize();
	}

	@Override
	public ActionSender getActionSender() {
		return null;
	}

	@Override
	public InterfaceState getInterfaceState() {
		return null;
	}

	@Override
	public Container getInventory() {
		return null;
	}

	@Override
	public boolean isNPC() {
		return true;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public int getCombatCooldownDelay() {
		return combatCooldownDelay;
	}

	/**
	 * @param combatCooldownDelay the combatCooldownDelay to setQuestStage
	 */
	public void setCombatCooldownDelay(int combatCooldownDelay) {
		this.combatCooldownDelay = combatCooldownDelay;
	}

	@Override
	public CombatAction getDefaultCombatAction() {
		return combatDefinition.getCombatAction();
	}

	@Override
	public Location getCentreLocation() {
		return Location.create(getLocation().getX() + (int) Math.floor(getWidth() / 2), getLocation().getY() + (int) Math.floor(getHeight() / 2), getLocation().getZ());
	}

	@Override
	public boolean canHit(Mob victim, boolean messages) {
		return combatDefinition != null;
	}

	@Override
	public boolean isAutoRetaliating() {
		return true;
	}

	@Override
	public int getProjectileLockonIndex() {
		return getIndex() + 1;
	}

	@Override
	public double getProtectionPrayerModifier() {
		return 0; //* 0 to remove the entire hit
	}

	@Override
	public String getDefinedName() {
		return definition.getName();
	}

	@Override
	public String getUndefinedName() {
		return "";
	}

	@Override
	public Animation getAttackAnimation() {
		return combatDefinition.getAttack();
	}

	@Override
	public Animation getDeathAnimation() {
		return combatDefinition.getDeath();
	}

	@Override
	public Animation getDefendAnimation() {
		return combatDefinition.getDefend();
	}

	@Override
	public Spell getAutocastSpell() {
		return combatDefinition.getSpell();
	}

	@Override
	public void setAutocastSpell(Spell spell) {
		if(spell != null) {
			combatDefinition.setSpell(spell);
		}
	}

	@Override
	public void setDefaultAnimations() {
	}

	@Override
	/**
	 * @author Joe.melsha@live.com (Killer 99)
	 */

	public void dropLoot(final Mob mob) {
		World.getWorld().submit(new Task() {
			@Override
			public void execute(GameEngine context) {
				long startTime = System.nanoTime();
				long startTimeMS = System.currentTimeMillis();
				NPCDrop[] constantDrops = combatDefinition.getConstantDrops();
				if (constantDrops == null)
					return;
				NPCDrop[] randomDrops = combatDefinition.getRandomDrops();
				if (randomDrops == null)
					return;
			
				
				for(int i = 0; i < constantDrops.length; i++) {
					if(constantDrops[i].getFrequency() >= 1) {
						World.getWorld().createGroundItem(new GroundItem(mob.getUndefinedName(), constantDrops[i].getItem(), getLocation()), (Player) mob);	
						System.out.println("Dropped item " +  constantDrops[i].getItem().toString() + " at frequency " +  constantDrops[i].getFrequency());
					}
				}
		
				if(randomDrops.length > 0) {
					NPCDrop drop = null;
					while(drop == null) {
						NPCDrop randomDrop = randomDrops[random.nextInt(randomDrops.length)];
						boolean alwaysDrop = randomDrop.getFrequency() >= 1;
						if(!alwaysDrop && random.nextDouble() < randomDrop.getFrequency()) {
							drop = randomDrop;
						}
					}
					System.out.println("Dropped item " + drop.getItem().toString() + " at frequency " + drop.getFrequency());
					World.getWorld().createGroundItem(new GroundItem(mob.getUndefinedName(), drop.getItem(), getLocation()), (Player) mob);
				}
		
				System.out.println("Took " + (System.nanoTime() - startTime)+" nanoseconds (" + (System.currentTimeMillis() - startTimeMS) +" milliseconds)");
			}
		});
	}


	@Override
	public boolean isObject() {
		return false;
	}

	@Override
	public Graphic getDrawbackGraphic() {
		return combatDefinition.getDrawbackGraphic();
	}

	@Override
	public int getProjectileId() {
		return combatDefinition.getProjectileId();
	}

}
