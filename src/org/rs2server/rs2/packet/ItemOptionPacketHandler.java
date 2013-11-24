package org.rs2server.rs2.packet;

import java.util.logging.Logger;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.impl.ConsumeItemAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Consumables.Drink;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.quests.impl.DwarfCannonQuest;
import org.rs2server.rs2.model.quests.impl.WerewolfQuest;
import org.rs2server.rs2.model.region.Tile;
import org.rs2server.rs2.model.skills.Crafting;
import org.rs2server.rs2.model.skills.Crafting.Gem;
import org.rs2server.rs2.model.skills.Crafting.Staff;
import org.rs2server.rs2.model.skills.Crafting.Hides;
import org.rs2server.rs2.model.skills.Herblore;
import org.rs2server.rs2.model.skills.Smithing;
import org.rs2server.rs2.model.skills.Fletching.ArrowTip;
import org.rs2server.rs2.model.skills.Fletching.Log;
import org.rs2server.rs2.model.skills.Herblore.Herb;
import org.rs2server.rs2.model.skills.Herblore.HerbloreType;
import org.rs2server.rs2.model.skills.Herblore.PrimaryIngredient;
import org.rs2server.rs2.model.skills.Herblore.SecondaryIngredient;
import org.rs2server.rs2.model.skills.Smithing.Bar;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;


/**
 * Remove item options.
 *
 * @author Graham Edgecombe
 */
public class ItemOptionPacketHandler implements PacketHandler {

    /**
     * The logger instance.
     */
    private static final Logger logger = Logger.getLogger(ItemOptionPacketHandler.class.getName());

    /**
     * Option drop/destroy opcode.
     */
    private static final int OPTION_DROP_DESTROY = 71;

    /**
     * Option pickup opcode.
     */
    private static final int OPTION_PICKUP = 105;

    /**
     * Option examine opcode.
     */
    private static final int OPTION_EXAMINE = 47;

    /**
     * Option 1 opcode.
     */
    private static final int OPTION_1 = 241;

    /**
     * Option 2 opcode.
     */
    private static final int OPTION_2 = 107;

    /**
     * Option 3 opcode.
     */
    private static final int OPTION_3 = 102;

    /**
     * Option 4 opcode.
     */
    private static final int OPTION_4 = 55;

    /**
     * Option 5 opcode.
     */
    private static final int OPTION_5 = 125;

    /**
     * Click 1 opcode.
     */
    private static final int CLICK_1 = 89;

    /**
     * Item on item opcode.
     */
    private static final int ITEM_ON_ITEM = 206;

    @Override
    public void handle(Player player, Packet packet) {
        if (player.getAttribute("cutScene") != null) {
            return;
        }
        switch (packet.getOpcode()) {
            case OPTION_DROP_DESTROY:
                handleItemOptionDrop(player, packet);
                break;
            case OPTION_PICKUP:
                handleItemOptionPickup(player, packet);
                break;
            case OPTION_EXAMINE:
                handleItemOptionExamine(player, packet);
                break;
            case OPTION_1:
                handleItemOption1(player, packet);
                break;
            case OPTION_2:
                handleItemOption2(player, packet);
                break;
            case OPTION_3:
                handleItemOption3(player, packet);
                break;
            case OPTION_4:
                handleItemOption4(player, packet);
                break;
            case OPTION_5:
                handleItemOption5(player, packet);
                break;
            case CLICK_1:
                handleItemOptionClick1(player, packet);
                break;
            case ITEM_ON_ITEM:
                handleItemOptionItem(player, packet);
                break;
        }
    }

    /**
     * Handles item option drop.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOptionDrop(Player player, Packet packet) {
        int interfaceId = packet.getInt2();
        int id = packet.getLEShortA();
        int slot = packet.getLEShort();
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemDrop", new Object[]{"ID: " + id});

        player.getActionSender().removeAllInterfaces();
        switch (interfaceId) {
            case Inventory.INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    Item item = player.getInventory().get(slot);
                    if (item != null && item.getId() != id) {
                        return;
                    }
                    player.getInventory().remove(item, slot);
                    World.getWorld().createGroundItem(new GroundItem(player.getName(), item, player.getLocation()), player);
                }
                break;
            default:
                logger.info("Unhandled item drop option : " + interfaceId + " - " + id + " - " + slot);
                break;
        }
    }

    /**
     * Handles item option pickup.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOptionPickup(final Player player, Packet packet) {
        final int id = packet.getShortA();
        int x = packet.getLEShort();
        int y = packet.getLEShort();
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        final Location location = Location.create(x, y, player.getLocation().getZ());

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemPickup", new Object[]{"ID: " + id, "Loc: " + location});

        player.getActionSender().removeAllInterfaces();
        Action action = new Action(player, 0) {
            @Override
            public CancelPolicy getCancelPolicy() {
                return CancelPolicy.ALWAYS;
            }

            @Override
            public StackPolicy getStackPolicy() {
                return StackPolicy.NEVER;
            }

            @Override
            public AnimationPolicy getAnimationPolicy() {
                return AnimationPolicy.RESET_ALL;
            }

            @Override
            public void execute() {
                Tile tile = player.getRegion().getTile(location);
                for (GroundItem g : tile.getGroundItems()) {
                    if (g.getItem().getId() == id && g.isOwnedBy(player.getName())) {
                        if (player.getInventory().add(player.checkForSkillcape(g.getItem()))) {
                            World.getWorld().unregister(g);
                        } else {
                            player.getActionSender().sendMessage("Not enough space in inventory.");
                        }
                        break;
                    }
                }
                this.stop();
            }
        };
        player.addCoordinateAction(player.getWidth(), player.getHeight(), location, 0, 0, 0, action);
    }

    /**
     * Handles item option 1.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOption1(final Player player, Packet packet) {
        int slot = packet.getShort() & 0xFFFF;
        int interfaceValue = packet.getLEInt();
        int interfaceId = interfaceValue >> 16;
        int childId = interfaceValue & 0xFFFF;
        int id = packet.getLEShortA() & 0xFFFF;
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt1", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        Item item = null;

        switch (interfaceId) {
            case Equipment.INTERFACE:
            case Equipment.SCREEN:
                if (slot >= 0 && slot < Equipment.SIZE) {
                    item = player.getEquipment().get(slot);
                    if (!player.canEmote()) {//stops people unequipping during a skillcape emote.
                        return;
                    }
                    if (!Container.transfer(player.getEquipment(), player.getInventory(), slot, id)) {
                        player.getActionSender().sendMessage("Not enough space in inventory.");
                    } else {
                        if (item != null && item.getEquipmentDefinition() != null) {
                            for (int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
                                player.getCombatState().setBonus(i, player.getCombatState().getBonus(i) - item.getEquipmentDefinition().getBonus(i));
                            }
                            player.getActionSender().sendBonuses();
                            if (slot == Equipment.SLOT_WEAPON) {
                                player.setDefaultAnimations();
                            }
                        }
                    }
                }
                break;
            case Bank.PLAYER_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    Bank.deposit(player, slot, id, 1);
                }
                break;
            case Bank.BANK_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Bank.SIZE) {
                    Bank.withdraw(player, slot, id, 1);
                }
                break;
            case Smithing.INTERFACE:
                if (player.getInterfaceAttribute("smithing_bar") != null) {
                    Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
                    int row = -1;
                    for (int i = 0; i < bar.getItems(childId - 146).length; i++) {
                        Item newItem = bar.getItems(childId - 146)[i];
                        if (newItem == null) {
                            continue;
                        }
                        if (newItem.getId() == id) {
                            item = newItem;
                            row = i;
                        }
                    }
                    if (item == null || row == -1) {
                        return;
                    }
                    player.getActionQueue().addAction(new Smithing(player, 1, item, childId - 146, row, bar));
                }
                break;
            default:
                logger.info("Unhandled item option 1 : " + id + " - " + slot + " - " + interfaceId + ".");
                break;
        }
    }

    /**
     * Handles item option 2.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOption2(Player player, Packet packet) {
        int slot = packet.getShortA() & 0xFFFF;
        int interfaceValue = packet.getInt();
        int interfaceId = (interfaceValue >> 16) & 0xFFFF;
        int childId = interfaceValue & 0xFFFF;
        int id = packet.getShort() & 0xFFFF;

        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt2", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        Item item = null;

        switch (interfaceId) {
            case Equipment.INTERFACE:
                if (slot >= 0 && slot < Equipment.SIZE) {
                    item = player.getEquipment().get(slot);
                    if (item != null) {
                        switch (item.getId()) {
                            case 2550:
                                player.getActionSender().sendMessage("Your Ring of Recoil can deal " + player.getCombatState().getRingOfRecoil() + " more points of damage before shattering.");
                                break;
                            default:
                                player.getActionSender().sendMessage("There is no way to operate that item.");
                                break;
                        }
                    }
                }
                break;
            case Bank.PLAYER_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    Bank.deposit(player, slot, id, 5);
                }
                break;
            case Bank.BANK_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Bank.SIZE) {
                    Bank.withdraw(player, slot, id, 5);
                }
                break;
            case Smithing.INTERFACE:
                if (player.getInterfaceAttribute("smithing_bar") != null) {
                    Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
                    int row = -1;
                    for (int i = 0; i < bar.getItems(childId - 146).length; i++) {
                        Item newItem = bar.getItems(childId - 146)[i];
                        if (newItem == null) {
                            continue;
                        }
                        if (newItem.getId() == id) {
                            item = newItem;
                            row = i;
                        }
                    }
                    if (item == null || row == -1) {
                        return;
                    }
                    player.getActionQueue().addAction(new Smithing(player, 5, item, childId - 146, row, bar));
                }
                break;
            default:
                logger.info("Unhandled item option 2 : " + id + " - " + slot + " - " + interfaceId + ".");
                break;
        }
    }

    /**
     * Handles item option 3.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOption3(Player player, Packet packet) {
        int id = packet.getLEShortA() & 0xFFFF;
        int interfaceValue = packet.getInt();
        int childId = (interfaceValue >> 16) & 0xFFFF;
        int interfaceId = interfaceValue & 0xFFFF;
        int slot = packet.getShort() & 0xFFFF;

        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt3", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        Item item = null;

        switch (interfaceId) {
            case Bank.PLAYER_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    Bank.deposit(player, slot, id, 10);
                }
                break;
            case Bank.BANK_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Bank.SIZE) {
                    Bank.withdraw(player, slot, id, 10);
                }
                break;
            case Smithing.INTERFACE:
                if (player.getInterfaceAttribute("smithing_bar") != null) {
                    Bar bar = (Bar) player.getInterfaceAttribute("smithing_bar");
                    int row = -1;
                    for (int i = 0; i < bar.getItems(childId - 146).length; i++) {
                        Item newItem = bar.getItems(childId - 146)[i];
                        if (newItem == null) {
                            continue;
                        }
                        if (newItem.getId() == id) {
                            item = newItem;
                            row = i;
                        }
                    }
                    if (item == null || row == -1) {
                        return;
                    }
                    player.getActionQueue().addAction(new Smithing(player, 10, item, childId - 146, row, bar));
                }
                break;
            default:
                logger.info("Unhandled item option 3 : " + id + " - " + slot + " - " + interfaceId + ".");
                break;
        }
    }

    /**
     * Handles item option 4.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOption4(Player player, Packet packet) {
        int slot = packet.getShort() & 0xFFFF;
        int interfaceId = packet.getLEShort() & 0xFFFF;
        packet.getLEShort();
        int id = packet.getLEShort() & 0xFFFF;
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt4", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        switch (interfaceId) {
            case Bank.PLAYER_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    Bank.deposit(player, slot, id, player.getInventory().getCount(id));
                }
                break;
            case Bank.BANK_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Bank.SIZE) {
                    Bank.withdraw(player, slot, id, player.getBank().getCount(id));
                }
                break;
            default:
                logger.info("Unhandled item option 4 : " + id + " - " + slot + " - " + interfaceId + ".");
                break;
        }
    }

    /**
     * Handles item option 5.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOption5(Player player, Packet packet) {
        int interfaceId = packet.getLEInt() >> 16;
        int slot = packet.getShortA() & 0xFFFF;
        int id = packet.getShortA() & 0xFFFF;
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOpt5", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        switch (interfaceId) {
            case Bank.PLAYER_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
                }
                break;
            case Bank.BANK_INVENTORY_INTERFACE:
                if (slot >= 0 && slot < Bank.SIZE) {
                    player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
                }
                break;
            default:
                logger.info("Unhandled item option 5 : " + id + " - " + slot + " - " + interfaceId + ".");
                break;
        }
    }

    /**
     * Handles item option examine.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOptionExamine(Player player, Packet packet) {
        int id = packet.getLEShortA() & 0xFFFF;

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemExamine", new Object[]{"ID: " + id});

        Item item = new Item(id);
        if (item.getDefinition() != null) {
            player.getActionSender().sendMessage(item.getDefinition().getDescription());
        }
    }

    /**
     * Handles item option 1.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOptionClick1(final Player player, Packet packet) {
        int id = packet.getLEShortA();
        int interfaceId = packet.getInt() >> 16;
        int slot = packet.getShortA();
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemClick1", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        Item item = null;

        switch (interfaceId) {
            case Inventory.INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    item = player.getInventory().get(slot);
                    if (item == null || id != item.getId()) {
                        return;
                    }
                    Herb herb = Herb.forId(id);
                    if (herb != null) {
                        if (player.getSkills().getLevelForExperience(Skills.HERBLORE) < herb.getRequiredLevel()) {
                            player.getActionSender().sendMessage("You cannot clean this herb. You need a Herblore level of " + herb.getRequiredLevel() + " to attempt this.");
                            return;
                        }
                        player.getActionSender().sendMessage("You clean the dirt from the " + ItemDefinition.forId(herb.getReward()).getName().toLowerCase() + ".");
                        player.getInventory().remove(new Item(herb.getId()), slot);
                        player.getInventory().add(new Item(herb.getReward()), slot);
                        return;
                    }
                    switch (item.getId()) {
                        case 10896:
                            if (player.getQuestStorage().hasFinished(new WerewolfQuest())) {
                                if (player.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
                                    player.getActionSender().sendMessage("You cannot be holding a weapon as a werewolf!");
                                    return;
                                }
                                player.playAnimation(Animation.create(836));
                                World.getWorld().submit(new Tickable(4) {
                                    @Override
                                    public void execute() {
                                        player.isWerewolf(true);
                                        player.playAnimation(Animation.create(-1));
                                        player.setPnpc(6212);
                                        player.setStandAnimation(Animation.create(6539));
                                        player.setWalkAnimation(Animation.create(6541));
                                        player.setRunAnimation(Animation.create(6541));
                                        player.setTurn180Animation(Animation.create(6541));
                                        player.setTurn90ClockwiseAnimation(Animation.create(6541));
                                        player.setTurn90CounterClockwiseAnimation(Animation.create(6541));
                                        player.setStandTurnAnimation(Animation.create(6541));
                                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                                        this.stop();
                                    }
                                });
                                World.getWorld().submit(new Tickable(6) {
                                    @Override
                                    public void execute() {
                                        player.playAnimation(Animation.create(6543));
                                        this.stop();
                                    }
                                });

                                World.getWorld().submit(new Tickable(50) {
                                    @Override
                                    public void execute() {
                                        player.isWerewolf(false);
                                        player.setPnpc(-1);
                                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                                        player.getActionSender().sendMessage("You turn back into a human");
                                        this.stop();
                                    }
                                });
                            } else {
                                player.getActionSender().sendMessage("You must complete Werewolf Vista in order to turn into a wolf.");
                            }
                            break;
                        case 6:
                            if (!player.getQuestStorage().hasFinished(new DwarfCannonQuest())) {
                                player.getActionSender().sendMessage("You must complete the Dwarf Cannon quest in order to set up.");
                                return;
                            }
                            for (GameObject obj : player.getRegion().getGameObjects()) {
                                if (obj != null && obj.getType() == 10 && obj.getLocation().equals(player.getLocation())) {
                                    player.getActionSender().sendMessage("You cannot set up a cannon here.");
                                    return;
                                }
                            }
                            player.setAttribute("cannon", new Cannon(player, player.getLocation()));
                            break;
                        default:
                            Action action = new ConsumeItemAction(player, item, slot);
                            action.execute();
                            break;
                    }
                    break;
                }
        }
    }

    /**
     * Handles item on item option.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleItemOptionItem(final Player player, Packet packet) {
        int slot = packet.getShort();
        int interfaceId = packet.getInt();
        int id = packet.getLEShortA();
        int usedWithSlot = packet.getLEShort();
        if (player.getCombatState().isDead()) {
            return;
        }
        player.getActionQueue().clearRemovableActions();

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOnItem", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        Item usedItem = null;
        Item withItem = null;

        switch (interfaceId) {
            case Inventory.INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    usedItem = player.getInventory().get(slot);
                    withItem = player.getInventory().get(usedWithSlot);
                    if (id != usedItem.getId()) {
                        return;
                    }
                    if (player.getCombatState().isDead()) {
                        return;
                    }
                    //<Kewley> Are we using a chisel?
                    if (usedItem.getId() == 1755 || withItem.getId() == 1755) {
                    	
                    	Item gemItem = null;
                    	
                    	//Find out which item was the uncutGem
                    	if (usedItem.getId() == 1755) {
                    		gemItem = withItem;
                    	} else {
                    		gemItem = usedItem;
                    	}

                    	Gem gem = Gem.forId(gemItem.getId());
                    	if (gem != null) {
                    		//Check this, not sure if it's 100%, I believe we define our own interface here
                            player.getActionSender().sendInterfaceModel(309, 2, 130, gem.getInitialItem());
                            player.getActionSender().sendString(309, 6, "<br><br><br><br>" + gem.name());
                            player.getActionSender().sendChatboxInterface(309);
                            player.setInterfaceAttribute("crafting_gem", gem);
                            return;
                    	}
                    }
                    //<Kewley> are we crafting a staff?
                    if (usedItem.getId() == 1379 || withItem.getId() == 1379) {
                    	
                    	Item staffItem = null;
                    	
                    	//Find out which item was the orb
                    	if (usedItem.getId() == 1379) {
                    		staffItem = withItem;
                    	} else {
                    		staffItem = usedItem;
                    	}
                    	System.out.println("staffItem = " +staffItem.getId());
                    	Staff staff = Staff.forId(staffItem.getId());
                    	if (staff != null) {
                    		//Set up the interface
                    		player.getActionSender().sendInterfaceModel(309, 2, 130, staff.getInitialItem());
                    		player.getActionSender().sendString(309, 6, "<br><br><br><br>" + staff.name());
                    		player.getActionSender().sendChatboxInterface(309);
                    		//Let the DB know the name that we set up and the value of the Staff enum
                    		player.setInterfaceAttribute("crafting_staff", staff);
                    		return;
                    	}
                    }
                    //TODO Leather interface is FUCKED UP!
                    if (usedItem.getId() == 1733 || withItem.getId() == 1733) {
                    	
                    	Item hideItem = null;
                    	
                    	if (usedItem.getId() == 1733) {
                    		hideItem = withItem;
                    	} else {
                    		hideItem = usedItem;
                    	}
                    	
                    	Hides hide = Hides.forId(hideItem.getId());
                    	
                    	if (hide != null) {
                    		interfaceId = 309;
                    		//Need to get the length to know how many objects we display on the interface
                    		if (hide.getGeneratedItem().length > 1) {
                    			interfaceId = 301 + hide.getGeneratedItem().length;
                                //System.out.println("interfaceId: " +interfaceId+ "   hideLength: " +hide.getProducts().length);
                    		}
                    		int hideLength = hide.getGeneratedItem().length;
                    		//Setup interface to have models and names corresponding to the models (vambs, body, chaps)
                    		for (int i = 0; i < hideLength; i++) {
                    			player.getActionSender().sendInterfaceModel(interfaceId, 2 + i, 160, hide.getGeneratedItem()[i]);
                    			//System.out.println("sendInterfaceModel( " +interfaceId+", " +(2+i)+ ", " +hide.getProducts()[i]+ ")");
                    			player.getActionSender().sendString(interfaceId, (interfaceId - 296) + (i * 4), "<br><br><br><br>" + ItemDefinition.forId(hide.getGeneratedItem()[i]).getName());
                    			//System.out.println("sendString( " +interfaceId+ ", " +((interfaceId - 296) + (i*4))+ ", " +ItemDefinition.forId(hide.getProducts()[i]).getName()+ ")");
                    		}
                    		player.getActionSender().sendChatboxInterface(interfaceId);
                    		player.setInterfaceAttribute("crafting_hide", hide);
                    		return;
                    	}
                    }
                    if (usedItem.getId() == 946 || withItem.getId() == 946) {
                        Item logItem = null;
                        if (usedItem.getId() == 946) {
                            logItem = withItem;
                        } else {
                            logItem = usedItem;
                        }
                        Log log = Log.forId(logItem.getId());
                        if (log != null) {
                            interfaceId = 309;
                            if (log.getItem().length > 1) {
                                interfaceId = 301 + log.getItem().length;
                            }
                            for (int i = 0; i < log.getItem().length; i++) {
                                player.getActionSender().sendInterfaceModel(interfaceId, 2 + i, 160, log.getItem()[i]);
                    			//System.out.println("sendInterfaceModel( " +interfaceId+", " +(2+i)+ ", " +log.getItem()[i]+ ")");
                                player.getActionSender().sendString(interfaceId, (interfaceId - 296) + (i * 4), "<br><br><br><br>" + ItemDefinition.forId(log.getItem()[i]).getName());
                    			//System.out.println("sendString( " +interfaceId+ ", " +((interfaceId - 296) + (i*4))+ ", " +ItemDefinition.forId(log.getItem()[i]).getName()+ ")");
                            }
                            player.getActionSender().sendChatboxInterface(interfaceId);
                            player.setInterfaceAttribute("fletching_log", log);
                            return;
                        }
                    }
                    if (usedItem.getId() == 53 || withItem.getId() == 53) {
                        Item arrowTips = null;
                        if (usedItem.getId() == 53) {
                            arrowTips = withItem;
                        } else {
                            arrowTips = usedItem;
                        }
                        ArrowTip tips = ArrowTip.forId(arrowTips.getId());
                        if (tips != null) {
                            if (player.getSkills().getLevelForExperience(Skills.FLETCHING) < tips.getLevelRequired()) {
                                player.getActionSender().sendMessage("You need a Fletching level of " + tips.getLevelRequired() + " to make these arrows.");
                                return;
                            }
                            int lowestAmount = player.getInventory().getCount(53) < player.getInventory().getCount(arrowTips.getId()) ? player.getInventory().getCount(53) : player.getInventory().getCount(arrowTips.getId());
                            if (player.getInventory().add(new Item(tips.getReward(), lowestAmount))) {
                                player.getInventory().remove(new Item(tips.getId(), lowestAmount));
                                player.getInventory().remove(new Item(53, lowestAmount));
                                player.getSkills().addExperience(Skills.FLETCHING, tips.getExperience() * lowestAmount);
                                player.getActionSender().sendMessage("You attach the arrow tips to the headless arrows.");
                            }
                            return;
                        }
                    }
                    if (usedItem.getId() == 227 || withItem.getId() == 227) {
                        Item primaryIngredient = null;
                        if (usedItem.getId() == 227) {
                            primaryIngredient = withItem;
                        } else {
                            primaryIngredient = usedItem;
                        }
                        PrimaryIngredient ingredient = PrimaryIngredient.forId(primaryIngredient.getId());
                        if (ingredient != null) {
                            if (player.getInventory().getCount(227) > 1 && player.getInventory().getCount(primaryIngredient.getId()) > 1) {
                                player.getActionSender().sendInterfaceModel(309, 2, 130, ingredient.getReward());
                                String itemName = "";
                                String leafName = ItemDefinition.forId(ingredient.getId()).getName().replaceAll(" leaf", "").replaceAll(" clean", "");
                                itemName = leafName + " potion (unf)";
                                player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
                                player.getActionSender().sendChatboxInterface(309);
                                player.setInterfaceAttribute("herblore_type", HerbloreType.PRIMARY_INGREDIENT);
                                player.setInterfaceAttribute("herblore_index", ingredient.getId());
                            } else {
                                player.getActionQueue().addAction(new Herblore(player, 1, ingredient, null, HerbloreType.PRIMARY_INGREDIENT));
                            }
                            return;
                        }
                    }
                    SecondaryIngredient ingredient = null;
                    for (SecondaryIngredient sIngredient : SecondaryIngredient.values()) {
                        if (sIngredient.getId() == withItem.getId() && sIngredient.getRequiredItem().getId() == usedItem.getId() || sIngredient.getId() == usedItem.getId() && sIngredient.getRequiredItem().getId() == withItem.getId()) {
                            ingredient = sIngredient;
                        }
                    }
                    if (ingredient != null) {
                        if (player.getInventory().getCount(ingredient.getId()) > 1 && player.getInventory().getCount(ingredient.getRequiredItem().getId()) > 1) {
                            player.getActionSender().sendInterfaceModel(309, 2, 130, ingredient.getReward());
                            player.getActionSender().sendString(309, 6, "<br><br><br><br>" + ItemDefinition.forId(ingredient.getReward()).getName());
                            player.getActionSender().sendChatboxInterface(309);
                            player.setInterfaceAttribute("herblore_type", HerbloreType.SECONDARY_INGREDIENT);
                            player.setInterfaceAttribute("herblore_index", ingredient.getIndex());
                        } else {
                            player.getActionQueue().addAction(new Herblore(player, 1, null, ingredient, HerbloreType.SECONDARY_INGREDIENT));
                        }
                        return;
                    }


                    Drink drink1 = Drink.forId(usedItem.getId());
                    Drink drink2 = Drink.forId(withItem.getId());
                    if (drink1 != null && drink2 != null) {
                        if (drink1 != drink2) {
                            player.getActionSender().sendMessage("You can't combine these two potions.");
                            return;
                        }
                        int index1 = -1;
                        int index2 = -1;
                        for (int i = 0; i < drink1.getIds().length; i++) {
                            if (drink1.getId(i) == usedItem.getId()) {
                                index1 = i + 1;
                                break;
                            }
                        }
                        for (int i = 0; i < drink2.getIds().length; i++) {
                            if (drink2.getId(i) == withItem.getId()) {
                                index2 = i + 1;
                                break;
                            }
                        }
                        int doses = index1 + index2;
                        int amount = 0;
                        Item endPotion1 = null;
                        Item endPotion2 = null;
                        if (doses < 5) {
                            endPotion1 = new Item(drink1.getId(doses - 1), 1);
                            endPotion2 = new Item(229, 1);
                            amount = doses;
                        } else {
                            endPotion1 = new Item(drink1.getId(3), 1);
                            amount = 4;
                            doses -= 4;
                            endPotion2 = new Item(drink1.getId(doses - 1), 1);
                        }
                        player.getInventory().remove(usedItem);
                        player.getInventory().remove(withItem);
                        player.getInventory().add(endPotion1, usedWithSlot);
                        player.getInventory().add(endPotion2, slot);
                        player.getActionSender().sendMessage("You have combined the liquid into " + amount + " doses.");
                        return;
                    }
                    player.getActionSender().sendMessage("Nothing interesting happens.");
                    break;
                }
        }
    }

}
