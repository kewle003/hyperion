package org.rs2server.rs2.model.combat.npcs;

import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;

public class TzTokJad extends AbstractCombatAction {
	/**
	 * The singleton instance.
	 */
	private static final TzTokJad INSTANCE = new TzTokJad();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * Default private constructor.
	 */
	public TzTokJad() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		RANGE,
		MAGIC
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		//System.out.println("In hit tztok");
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = null;
		
		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(random.nextInt(3)) {
			case 0:
				style = CombatStyle.MELEE;	
				//attacker.getActionSender().sendFollowing(victim, 1);
				break;
			case 1:
				style = CombatStyle.MAGIC;
				//attacker.getActionSender().sendFollowing(victim, 1);
				break;
			case 2:
				style = CombatStyle.RANGE;
				//attacker.getActionSender().sendFollowing(victim, 1);
				break;
			}
		} else {
			//For when the player is not within attacking distance
			switch(random.nextInt(2)) {
			case 0:
				style = CombatStyle.MAGIC;
				//attacker.getActionSender().sendFollowing(victim, 1);
				break;
			case 1:
				style = CombatStyle.RANGE;
				//attacker.getActionSender().sendFollowing(victim, 1);
			}
		}
		
		switch(style) {
		case MELEE:
			Animation anim = attacker.getAttackAnimation();
			if(random.nextInt(2) == 1) {
				anim = Animation.create(2655);
			}
			attacker.playAnimation(anim);
			
			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case MAGIC:
			attacker.playAnimation(Animation.create(2656));
			attacker.playGraphics(Graphic.create(1625, 0, 100));
			attacker.playGraphics(Graphic.create(1626, 3, 100));

			hitDelay = 2;
			blockAnimation = false;
			maxHit = 92;
			damage = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, true);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case RANGE:
			attacker.playAnimation(Animation.create(2652));
			
			hitDelay = 2;
			blockAnimation = false;
			maxHit = 92;
			damage = damage(maxHit, attacker, victim, AttackType.RANGE, Skills.MAGIC, Prayers.PROTECT_FROM_MISSILES, false, true);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			victim.playGraphics(Graphic.create(451, 0, 100));
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(8);
		attacker.getCombatState().setSpellDelay(8);
		
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}			
		});
		vengeance(attacker, victim, hit, 1);
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	@Override
	public int distance(Mob attacker) {
		return 5;
	}

}
