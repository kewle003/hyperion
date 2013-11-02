package org.rs2server.rs2.model.minigame.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPCDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.WalkingQueue;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.tickable.Tickable;

/**
 * This represents the Fight Caves minigame
 * @author mark
 * @credits 
 *
 */
//TODO Make the fight caves an event per player FIXED, when a player is put in FightCaves
//set is height to playerIndex()*4, also had to create an exit object that had this height.
//TODO Have monsters follow players
public class FightCaves extends AbstractMinigame {
	
	/**
	 * The maximum amount of players allowed
	 */
	public static final int MAXIMUM_SIZE = 1;
	
	/**
	 * The wave of the fight cave
	 */
	private Wave wave;
	
	/**
	 * Gets the height of the player
	 */
	private int height;
	
	
	/**
	 * Creates the exit
	 */
	private GameObject exit;
	
	/**
	 * The random number generator
	 */
	private static Random random = new Random();
	
	/**
	 * The current npcs, if there is one.
	 */
	private List<NPC> currentNPCs;
	
	/**
	 * The player (yes just one)
	 */
	private List<Player> participant;
	
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
	
	private final static int[][] COORDINATES = {{2403, 5094}, {2390, 5096}, 
												{2392, 5077}, {2408, 5080}, 
												{2413, 5108}, {2381, 5106}, 
												{2379, 5072}, {2420, 5082}};
	
	/**
	 * List of the npcs during a current wave
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
		  	 **/
	
	private enum Wave {
		ONE(0, new NPC[] { new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.EAST) }),
		TWO(1, new NPC[] { new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.EAST),
						   new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.NORTH)}),
		THREE(2, new NPC[] { new NPC(NPCDefinition.forId(TZ_KEK), null, null, null, WalkingQueue.EAST) }),
		FOUR(3, new NPC[] { new NPC(NPCDefinition.forId(TZ_KEK), null, null, null, WalkingQueue.EAST),
						    new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.NORTH) }),
		FIVE(4, new NPC[] { new NPC(NPCDefinition.forId(TZ_KEK ), null, null, null, WalkingQueue.EAST),
							new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.NORTH), 
							new NPC(NPCDefinition.forId(TZ_KIH), null, null, null, WalkingQueue.NORTH) }),
		SIX(5, new NPC[] { new NPC(NPCDefinition.forId(TZ_KEK), null, null, null, WalkingQueue.EAST), 
						   new NPC(NPCDefinition.forId(TZ_KEK), null, null, null, WalkingQueue.NORTH)}),
		SEVEN(6, new NPC[] { new NPC(NPCDefinition.forId(TOK_XIL), null, null, null, WalkingQueue.EAST) });				   
		
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
	
	/**
	 * Fight Caves constructor. Notice how we take note of
	 * the player's height. This is how we make Fight Caves
	 * separate for each player. In doing so, we also have to
	 * create a new exit object for each player.
	 * @param player
	 */
	public FightCaves(Player player) {	
		player.getCombatState().resetPrayers();
		init();
		height = player.getIndex()*4;
		exit = new GameObject(Location.create(2412, 5118,height),9357,10,0,true);
		World.getWorld().register(exit);
		participant = new ArrayList<Player>();
		participant.add(player);
		start();
	}
	
	/**
	 * This will spawn the next NPC's wave
	 * @param wave The current Wave enum value
	 */
	//TODO Make the NPC's follow and attack the player rather than freeze
	public void spawnWave(Wave wave) {
		NPC[] waveNPCs = wave.getNPCs();
		currentNPCs = new ArrayList<NPC>();
		participant.get(0).getActionSender().sendMessage("Wave: " +wave.getId());
		if (wave.getId() == 63) {
			
		}
		for (NPC npc : waveNPCs) {
			int randomCoord = random.nextInt(8);
			NPC newNPC = new NPC(npc.getDefinition(), Location.create(COORDINATES[randomCoord][0], COORDINATES[randomCoord][1], height), Location.create(2365, 5060, height), Location.create(2430, 5125, height), npc.getDirection());
			currentNPCs.add(newNPC);
			World.getWorld().register(newNPC);
			newNPC.getCombatState().startAttacking(participant.get(0), false); //In beta
			//newNPC.getActionSender().sendFollowing(participant.get(0), 5);
		}

	}
	
	@Override
	public void start() {
		super.start();
		wave = Wave.ONE;
		for(Player player : participant) {
			player.resetVariousInformation();
			player.setTeleportTarget(Location.create(2413, 5116, height));
			player.getActionSender().removeAllInterfaces();
			DialogueManager.openDialogue(player, 229);
		}
		spawnWave(wave);
	}
	
	@Override
	public void quit(Player player) {
		World.getWorld().unregister(exit, true);
		super.quit(player);
		//end();
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
			if (npc.getDefinition().getId() == TZ_KEK) {
				NPC tz_kekSpawn1 = new NPC(NPCDefinition.forId(TZ_KEK_SPAWN), Location.create(victim.getLocation().getX()+2, victim.getLocation().getY()+2,height),
						null, null, WalkingQueue.EAST );
				NPC tz_kekSpawn2 = new NPC(NPCDefinition.forId(TZ_KEK_SPAWN), Location.create(victim.getLocation().getX()-2, victim.getLocation().getY()-2,height),
						null, null, WalkingQueue.EAST );
				currentNPCs.add(tz_kekSpawn1);
				currentNPCs.add(tz_kekSpawn2);
				World.getWorld().register(tz_kekSpawn1);
				World.getWorld().register(tz_kekSpawn2);
			}
			if(currentNPCs.size() < 1) {
				wave = Wave.forId(this.wave.getId() + 1);
				//This implies we have gone through all the waves
				if(wave == null) {
					participant.get(0).playAnimation(Animation.CHEER);
					participant.get(0).setFightCavesCompleted();
					//Set delay so player will play the Cheer animation
					World.getWorld().submit(new Tickable(5) {
						public void execute() {
							end();
							DialogueManager.openDialogue(participant.get(0), 231);
							this.stop();
						}
					});
					return; //finished
					}
				spawnWave(wave);
			}
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
		return Boundary.create(getName(), Location.create(2365, 5060, height), Location.create(2430, 5125, height));
	}
	
	@Override
	public void end() {
		super.end();
		for(NPC npc : currentNPCs) {
			World.getWorld().unregister(npc);
		}
	}

}
