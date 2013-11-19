package org.rs2server.rs2.model.skills;

import java.util.HashMap;
import java.util.Map;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;

//TODO: ALOT, need to make this applicable for all crafting items not just Gems
//Gems - done
//Hides - in progress
//Staff - need animation then done
//Jewelry - not even started
public class Crafting extends ProductionAction{
	
	/**
	 * Represents what armor we are crafting (vambs, chaps, body)
	 */
	private int hideIndex;
	
	/**
	 * The amount of items to produce.
	 */
	private int productionCount;
	
	/**
	 * The gem we are crafting.
	 */
	private Gem gem;
	
	/**
	 * The staff we are crafting.
	 */
	private Staff staff;
	
	/**
	 * The hide we are crafting
	 */
	private Hides hide;
	
	/**
	 * The jewelry we wish to craft
	 */
	private Jewelry jewelry;
	
	/**
	 * The enum value to let our methods know
	 * what crafting item we are crafting (hide, gem, staff).
	 */
	private CraftingItem craftingItem;
	
	/**
	 * How we query which type we have
	 * @author mark
	 *
	 */
	public static enum CraftingItem {
		GEM,
		HIDE,
		STAFF,
		JEWELRY //TODO
	}
	
	/**
	 * There has to be a better way about this, maybe create a
	 * datafile that holds on to Gem, Staff, and Hides
	 * Note: tried polymorphism did not work
	 * @param mob
	 * @param productionCount
	 * @param gem
	 */
	public Crafting(Mob mob, int productionCount, Gem gem, CraftingItem type) {
		super(mob);
		this.productionCount = productionCount;
		this.gem = gem;
		this.craftingItem = type;
	}
	
	public Crafting(Mob mob, int productionCount, Staff staff, CraftingItem type) {
		super(mob);
		this.productionCount = productionCount;
		this.staff = staff;
		this.craftingItem = type;
	}
	
	public Crafting(Mob mob, int productionCount, Hides hide, int hideIndex, CraftingItem type) {
		super(mob);
		this.productionCount = productionCount;
		this.hide = hide;
		this.craftingItem = type;
	}
	
	public Crafting(Mob mob, int productionCount, Jewelry jewelry, CraftingItem type) {
		super(mob);
		this.productionCount = productionCount;
		this.jewelry = jewelry;
		this.craftingItem = type;
	}
	
	
	/**
	 * Represents the type of Gem to craft.
	 * @author mark
	 *
	 */
	public static enum Gem {
		SAPPHIRE(1623, 1607, 20, 888, 50.0),
		EMERALD(1621, 1605, 27, 889, 67.0),
		RUBY(1619, 1603, 34, 887, 85.0),
		DIAMOND(1617, 1601, 43, 886, 107.5),
		DRAGONSTONE(1631, 1615, 55, 885, 137.5),
		ONYX(6571, 6573, 67, 885, 168);
		
		/**
		 * The item id for uncut Gem
		 */
		private int unCut;
		
		/**
		 * The item id for cut Gem
		 */
		private int product;
		
		/**
		 * The level required
		 */
		private int levelRequired;
		
		/**
		 * The animation for crafting
		 */
		private int animation;
		
		/**
		 * The experience granted.
		 */
		private double experience;
		
		/**
		 * A map of the item ids to uncut Gems
		 */
		private static HashMap <Integer, Gem> gems = new HashMap<Integer, Gem>();
		
		/**
		 * Gets the uncut Gem by an item id
		 * @param id the item id
		 * @return The uncut Gem, or <code>null</code> if the object is not an uncut Gem
		 */
		public static Gem forId(int id) {
			return gems.get(id);
		}
		
		private Gem(int unCut, int product, int level, int animation, double experience) {
			this.unCut = unCut;
			this.product = product;
			this.levelRequired = level;
			this.animation = animation;
			this.experience = experience;
		}
		
		/**
		 * Populates the Gem map
		 */
		static {
			for (Gem g : Gem.values()) {
				gems.put(g.getUnCut(), g);
			}
		}
		
		/**
		 * The uncut id
		 * @return uncut item id
		 */
		public int getUnCut() {
			return unCut;
		}
		
		/**
		 * The cut gem
		 * @return cut gem item id
		 */
		public int getProduct() {
			return product;
		}
		
		/**
		 * @return the levelRequired
		 */
		public int getLevelRequired() {
			return levelRequired;
		}
		
		/**
		 * @return the animation
		 */
		public int getAnimation() {
			return animation;
		}
		
		/**
		 * @return the experience for crafting the Gem
		 */
		public double getExperience() {
			return experience;
		}
		
	}
	
	/**
	 * Represents the type of Staff to craft.
	 * @author mark
	 *
	 */
	public enum Staff {
		WATER(571, 1395, 54, 0, 100),
		EARTH(575, 1399, 58, 0, 112.5),
		FIRE(569, 1393, 62, 0, 125),
		AIR(573, 1397, 66, 0, 137.5);
		
		/**
		 * The orb id we put on our battle staff
		 */
		private int orb;
		
		/**
		 * The battle staff id
		 */
		private int reward;
		
		/**
		 * The level required to craft the staff
		 */
		private int level;
		
		/**
		 * The experience for crafting the battle staff
		 */
		private double experience;
		
		/**
		 * The animation id when crafting the staff
		 */
		private int animation;
		
		/**
		 * Where we store our enum values
		 */
		private static Map<Integer, Staff> staffs = new HashMap<Integer, Staff>();

		private Staff(int orb, int reward, int level, int animation, double experience) {
			this.orb = orb;
			this.reward = reward;
			this.level = level;
			this.experience = experience;
		}
		
		/**
		 * The id of the particular orb
		 * @param item id
		 * @return the Staff enum value (WATER, AIR,...)
		 */
		public static Staff forId(int item) { 
			return staffs.get(item); 
		}
		
		/**
		 * Populate our HashMap with enum values
		 */
		static {
			for(Staff s : Staff.values()) {
				staffs.put(s.getOrb(), s);
			}
		}
		
		/**
		 * The item id of the orb
		 * @return
		 */
		public int getOrb() {
			return orb;
		}
		
		/**
		 * The battle staff item id
		 * @return
		 */
		public int getReward() {
			return reward;
		}
		
		/**
		 * The animation id
		 * @return
		 */
		public int getAnimation() {
			return animation;
		}
		
		/**
		 * The level required to craft the staff
		 * @return
		 */
		public int getLevelRequired() {
			return level;
		}
		
		/**
		 * The experience gained for crafting the staff
		 * @return
		 */
		public double getExperience() {
			return experience;
		}
		
	}
	
	/**
	 * Represents the type of hide to craft.
	 * @author mark
	 *
	 */
	public enum Hides {
		GREEN_DRAGONHIDE(1745, new int[] {1135, 1065, 1099}, new int[] {3, 1, 2}, new int[] {63, 57, 60}, new double[] {186.0, 62.0, 124.0}),
        BLUE_DRAGONHIDE(2505, new int[] {2499, 2487, 2493}, new int[] {3, 1, 2}, new int[] {71, 66, 68}, new double[] {210.0, 70.0, 140.0}),
        RED_DRAGONHIDE(2507, new int[] {2501, 2489, 2495}, new int[] {3, 1, 2}, new int[] {77, 73, 75}, new double[] {234.0, 78.0, 124.0}),
        BLACK_DRAGONHIDE(2509, new int[] {2503, 2491, 2497}, new int[] {3, 1, 2}, new int[] {84, 79, 82}, new double[] {258.0, 86.0, 172.0}),
        SOFT_LEATHER(1741, new int[] {1129, 1063, 1095, 1167, 1061, 1059, 1169}, new int[] {1, 1, 1, 1, 1, 1, 1}, new int[] {14, 11, 18, 9, 7, 1, 38}, new double[] {25.0, 22.0, 27.0, 18.5, 16.3, 13.8, 37.0}),
        HARD_LEATHER(1743, new int[] {1131}, new int[] {1}, new int[] {28}, new double[] {35.0}),
        SNAKESKIN(6289, new int[] {6322, 6324, 6330, 6326, 6328}, new int[] {15, 12, 8, 5, 6}, new int[] {53, 51, 47, 48, 45}, new double[] {55.0, 50.0, 35.0, 45.0, 30.0});
        
		/**
		 * This represents the hide item id
		 */
		private int hide;
		
		/**
		 * An array to represent which product we will return (vambs, chaps, body)
		 */
		private int[] products;
		
		/**
		 * An array to represent how many hides we consume
		 */
		private int[] amounts;
		
		/**
		 * An array to represent the level requirement for making (vambs, chaps, body)
		 */
		private int[] level;
		
		/**
		 * The experience gained from making (vambs, chaps, body)
		 */
		private double[] experience;
		
		/**
		 * The HashMap where we store our ENUMS
		 */
		private static Map<Integer, Hides> hides = new HashMap<Integer, Hides>();
		
		private Hides(int hide, int[] products, int[] amounts, int[] level, double[] experience) {
			this.hide = hide;
            this.products = products;
            this.amounts = amounts;
            this.level = level;
            this.experience = experience;
		}
		
		/**
		 * Gets the ENUM value
		 * @param hide
		 * @return
		 */
		public static Hides forId(int hide) {
            return hides.get(hide);
        }
		
		/**
		 * Populate our HashMap with our ENUM values
		 */
		static {
			for(Hides hide : Hides.values()) {
                hides.put(hide.hide, hide);
            }
		}
		
		/**
		 * This will grab the hide we are crafting
		 * @return
		 */
        public int getHide() {
            return hide;
        }
        
        /**
         * This will grab the product we return (vambs, chaps, body)
         * @param index
         * @return
         */
        public int[] getProducts() {
            return products;
        }
        
        /**
         * This method will tell us how many hides we remove per product we make
         * @param index
         * @return
         */
        public int[] getAmounts() {
            return amounts;
        }
        
        /**
         * This method will get the required level for the product we wish to craft
         * @param index
         * @return
         */
        public int[] getLevel() {
            return level;
        }
        
        /**
         * This method will return the experience gained from crafting the hide
         * @param index
         * @return
         */
        public double[] getExp() {
            return experience;
        }
        
	}
	
	//TODO (Maybe in Smithing)
	public enum Jewelry {
		;
	}

	@Override
	//TODO: Figure out different times for GEM, HIDE, and STAFF
	public int getCycleCount() {
		switch(craftingItem) {
		case GEM:
			return 2;
		case HIDE:
			return 3;
		case STAFF:
			return 3;
		case JEWELRY:
			return 2;
		}
		return 3;
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public Item[] getRewards() {
		switch(craftingItem) {
		case GEM:
			return new Item[] {new Item(gem.getProduct(), 1) };
		case HIDE:
			return new Item[] {new Item(hide.getProducts()[hideIndex], 1)};
		case STAFF:
			return new Item[] {new Item(staff.getReward(), 1)};
		case JEWELRY:
			return null;
		}
		return null;
	}

	@Override
	public Item[] getConsumedItems() {
		switch(craftingItem) {
		case GEM:
			return new Item[] { new Item(gem.getUnCut(), 1) };
		case HIDE:
			return new Item[] {new Item(hide.getHide(), hide.getAmounts()[hideIndex])};
		case STAFF:
			return new Item[] {new Item(staff.getOrb(), 1), new Item(1379, 1)};
		case JEWELRY:
			return null;
		}
		return null;
		
	}

	@Override
	public int getSkill() {
		return Skills.CRAFTING;
	}

	@Override
	public int getRequiredLevel() {
		switch(craftingItem) {
		case GEM:
			return gem.getLevelRequired();
		case HIDE:
			return hide.getLevel()[hideIndex];
		case STAFF:
			return staff.getLevelRequired();
		case JEWELRY:
			return 0;
		}
		return 0;
		
	}

	@Override
	public double getExperience() {
		switch(craftingItem) {
		case GEM:
			return gem.getExperience();
		case HIDE:
			return hide.getExp()[hideIndex];
		case STAFF:
			return staff.getExperience();
		case JEWELRY:
			return 0.0;
		}
		return 0.0;
		
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Crafting level of " + getRequiredLevel() + " to craft this.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		String prefix = "a";
		String suffix = "";
		char first = ' ';
		String retVal = "";
		
		switch(craftingItem) {
		case GEM:
			first = ItemDefinition.forId(gem.getUnCut()).getName().toLowerCase().charAt(0);
			break;
		case HIDE:
			first = ItemDefinition.forId(hide.getProducts()[hideIndex]).getName().toLowerCase().charAt(0);
			break;
		case STAFF:
			first = ItemDefinition.forId(staff.getReward()).getName().toLowerCase().charAt(0);
			break;
		case JEWELRY:
			first = ' ';
			break;
		}
		
		if (first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u') {
			prefix = "an";
		}
		
		switch(craftingItem) {
		case GEM:
			retVal = "You successfully craft " + prefix + " " + ItemDefinition.forId(gem.getUnCut()).getName().toLowerCase() + ".";
			break;
		case HIDE:
			retVal = "You successfully craft " + prefix + " " + ItemDefinition.forId(hide.getProducts()[hideIndex]).getName().toLowerCase() + ".";
			break;
		case STAFF:
			retVal = "You successfully craft " + prefix + " " + ItemDefinition.forId(staff.getReward()).getName().toLowerCase() + ".";
			break;
		case JEWELRY:
			retVal = "";
			break;
		}
		
		return retVal;
	}

	@Override
	public Animation getAnimation() {
		switch(craftingItem) {
		case GEM:
			return Animation.create(gem.getAnimation());
		case HIDE:
			//return Animation.create(hide.getAnimation());
		case STAFF:
			//return Animation.create(staff.getAnimation());
		case JEWELRY:
		}
		return null;
		
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public boolean canProduce() {
		return true;
	}

}
