package org.rs2server.rs2.model.minigame.impl;

import java.util.List;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.minigame.Minigame;
import org.rs2server.rs2.tickable.Tickable;


/**
 * Provides a skeletal implementation for <code>Minigame</code>s on which
 * other code should base their code off.
 * <p>
 * This implementation contains code common to ALL implementations.
 * @author Michael Bull
 *
 */

public class AbstractMinigame implements Minigame {
	
	/**
	 * Initializes the minigames instance.
	 */
	public void init() {
		if(getItemSafety() == ItemSafety.SAFE) {
			BoundaryManager.addBoundary(Boundary.create("SafeZone", getBoundary().getBottomLeft(), getBoundary().getTopRight())); //we only need to add safe zones as it would be default to lose items
		}
		BoundaryManager.addBoundary(getBoundary());
		if(getGameCycle() != null) {
			World.getWorld().submit(getGameCycle());
		}
	}

	@Override
	public void end() {
		for(Player participant : getPlayer()) {
			participant.setTeleportTarget(getStartLocation());
			participant.resetVariousInformation();
		}
		if(getGameCycle() != null) {
			getGameCycle().stop();
		}
	}

	@Override
	public void quit(Player player) {
		player.setMinigame(null);
		player.setAttribute("temporaryHeight", null);
		player.setTeleportTarget(getStartLocation());
		player.setLocation(getStartLocation());
		player.resetVariousInformation();
		if(getPlayer() != null) {
			getPlayer().remove(player);
			if(getPlayer().size() < 1) {
				end();
			}
		}
	}

	@Override
	public Boundary getBoundary() {
		return null;
	}

	@Override
	public ItemSafety getItemSafety() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<Player> getPlayer() {
		return null;
	}

	@Override
	public void start() {
	}

	@Override
	public Tickable getGameCycle() {
		return null;
	}

	@Override
	public Location getStartLocation() {
		return null;
	}

	@Override
	public boolean deathHook(Player player) {
		return false;
	}

	@Override
	public void movementHook(Player player) {
		if(!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), getBoundary())) {
			System.out.println("We called the Abstract class movementHook");
			quit(player);
		}
	}

	@Override
	public void killHook(Player player, Mob victim) {
	}

	@Override
	public boolean attackMobHook(Player player, Mob victim) {
		return true;
	}

}
