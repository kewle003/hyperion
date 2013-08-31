package org.rs2server.rs2.model.minigame.impl;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.WalkingQueue;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.tickable.Tickable;

public class FightCaves extends AbstractMinigame {
	
	/**
	 * Logger instance
	 */
	private static final Logger logger = Logger.getLogger(FightCaves.class.getName());
	
	/**
	 * The maximum amount of players allowed
	 */
	public static final int MAXIMUM_SIZE = 1;
	
	/**
	 * The wave of the fight cave
	 */
	private Wave wave;
	
	
	/**
	 * The current npcs, if there is one.
	 */
	private List<NPC> currentNPCs;
	
	/**
	 * The player (yes just one)
	 */
	private List<Player> participant;
	
	/**
	 * Checks if the player has completed a given wave
	 */
	private int currentWave = 0;
	
	/**
	 * The fight caves start flag
	 */
	private boolean fightCavesStarted = false;
	
	/**
	 * All of the NPC id's
	 */
	public static final int TZ_KIH = 2627;
	public static final int TZ_KEK_SPAWN = 2738;
	public static final int TZ_KEK = 2630;
	public static final int TOK_XIL = 2631;
	public static final int YT_MEJKOT = 2741;
	public static final int KET_ZEK = 2743;
	public static int TZTOK_JAD = 2745;
	
	/**
	 * List of the npcs during a current wave
	 */
	private final int[][] wa = {{TZ_KIH},
		  	  {TZ_KIH,TZ_KIH},
		  	  {TZ_KEK},
		  	  {TZ_KEK,TZ_KIH},
		  	  {TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {TZ_KEK,TZ_KEK},
		  	  {TOK_XIL},
		  	  {TOK_XIL,TZ_KIH},
		  	  {TOK_XIL,TZ_KIH,TZ_KIH},
		  	  {TOK_XIL,TZ_KEK},
		  	  {TOK_XIL,TZ_KEK,TZ_KIH},
		  	  {TOK_XIL,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {TOK_XIL,TZ_KEK,TZ_KEK},
		  	  {TOK_XIL,TOK_XIL},
		  	  {YT_MEJKOT},
		  	  {YT_MEJKOT,TZ_KIH},
		  	  {YT_MEJKOT,TZ_KIH,TZ_KIH},
		  	  {YT_MEJKOT,TZ_KEK},
		  	  {YT_MEJKOT,TZ_KEK,TZ_KIH},
		  	  {YT_MEJKOT,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {YT_MEJKOT,TZ_KEK,TZ_KEK},
		  	  {YT_MEJKOT,TOK_XIL},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KIH},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KIH,TZ_KIH},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KEK},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KIH},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KEK},
		  	  {YT_MEJKOT,TOK_XIL,TOK_XIL},
		  	  {YT_MEJKOT,YT_MEJKOT},
		  	  {KET_ZEK},
		  	  {KET_ZEK,TZ_KIH},
		  	  {KET_ZEK,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,TZ_KEK},
		  	  {KET_ZEK,TZ_KEK,TZ_KIH},
		  	  {KET_ZEK,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,TZ_KEK,TZ_KEK},
		  	  {KET_ZEK,TOK_XIL},
		  	  {KET_ZEK,TOK_XIL,TZ_KIH},
		  	  {KET_ZEK,TOK_XIL,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,TOK_XIL,TZ_KEK},
		  	  {KET_ZEK,TOK_XIL,TZ_KEK,TZ_KIH},
		  	  {KET_ZEK,TOK_XIL,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,TOK_XIL,TZ_KEK,TZ_KEK},
		  	  {KET_ZEK,TOK_XIL,TOK_XIL},
		  	  {KET_ZEK,YT_MEJKOT},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KEK},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KEK,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TZ_KEK,TZ_KEK},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KEK},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KIH,TZ_KIH},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TZ_KEK,TZ_KEK},
		  	  {KET_ZEK,YT_MEJKOT,TOK_XIL,TOK_XIL},
		  	  {KET_ZEK,YT_MEJKOT,YT_MEJKOT},
		  	  {KET_ZEK,KET_ZEK},
		  	  {TZTOK_JAD}};;
		  	 
	private enum Wave {
		ONE(0, new NPC[] { new NPC(NPCDefinition.forId(TZ_KIH), Location.create(2403, 5094, 0), null, null, WalkingQueue.EAST) });
		
		/**
		 * The list of waves of npcs.
		 */
		public static List<Wave> waves = new ArrayList<Wave>();
		
		public static Wave forId(int wave) {
			if(wave >= waves.size()) {
				return null;
			}
			return waves.get(wave);
		}
		
		/**
		 * Populates the wave list.
		 */
		static {
			for(Wave wave : Wave.values()) {
				waves.add(wave);
			}
		}
		
		/**
		 * The id of this wave.
		 */
		private int id;
		
		/**
		 * The npc spawned in this wave.
		 */
		private NPC[] npcs;
		
		private Wave(int id, NPC[] npcs) {
			this.id = id;
			this.npcs = npcs;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}


		/**
		 * @return the npc
		 */
		public NPC[] getNPCs() {
			return npcs;
		}
	}
	
	public FightCaves(Player player) {	
		player.getCombatState().resetPrayers();
		init();
		participant = new ArrayList<Player>();
		participant.add(player);
		start();
	}
	
	/**
	 * This will spawn the next NPC's wave
	 * @param player
	 */
	public void spawnWave(Wave wave) {
		NPC[] waveNPCs = wave.getNPCs();
		currentNPCs = new ArrayList<NPC>();
		participant.get(0).getActionSender().sendMessage("Wave: " +wave.getId());
		for (NPC npc : waveNPCs) {
			NPC newNPC = new NPC(npc.getDefinition(), Location.create(npc.getSpawnLocation().getX(), npc.getSpawnLocation().getY(), 0), null, null, npc.getDirection());
			currentNPCs.add(newNPC);
			World.getWorld().register(newNPC);
			newNPC.getCombatState().startAttacking(participant.get(0), false);
			
		}

	}
	
	@Override
	public void start() {
		super.start();
		wave = Wave.ONE;
		for(Player player : participant) {
			player.resetVariousInformation();
			player.setTeleportTarget(Location.create(2413, 5116, 0));
		}
		spawnWave(wave);
	}
	
	@Override
	public void quit(Player player) {
		super.quit(player);
	}
	
	@Override
	public ItemSafety getItemSafety() {
		return ItemSafety.SAFE;
	}
	
	@Override
	public Location getStartLocation() {
		return Location.create(2438, 5169, 0);
	}
	
	@Override
	public boolean deathHook(Player player) {
		quit(player);
		end();
		player.setLocation(getStartLocation());
		player.setTeleportTarget(getStartLocation());
		return true;
	}
	
	@Override
	public void killHook(Player player, Mob victim) {
		super.killHook(player, victim);
		if(victim.isNPC()) {
			NPC npc = (NPC) victim;
			currentNPCs.remove(npc);
			if(currentNPCs.size() < 1) {
				wave = Wave.forId(this.wave.getId() + 1);
				if(wave == null) {
					participant.get(0).setFightCavesCompleted();
					end();
					}
					return;//finished
				}
				spawnWave(wave);
			}
	}
	
	@Override
	public void movementHook(Player player) {
		super.movementHook(player);
	}
	
	@Override
	public String getName() {
		return "Fight Caves";
	}
	
	@Override
	public List<Player> getPlayer() {
		return participant;
	}
	
	@Override
	public Tickable getGameCycle() {
		return null;
	}
	
	@Override
	public Boundary getBoundary() {
		//TODO correct coords
		//2430 5125
		//2365 5060
		return Boundary.create(getName(), Location.create(2365, 5060, 0), Location.create(2430, 5125, 0));
	}
	
	@Override
	public void end() {
		super.end();
		for(NPC npc : currentNPCs) {
			World.getWorld().unregister(npc);
		}
	}

}
