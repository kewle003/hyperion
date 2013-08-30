package org.rs2server.rs2.packet;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.impl.WieldItemAction;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Handles the 'wield' option on items.
 *
 * @author Graham Edgecombe
 */
public class WieldPacketHandler implements PacketHandler {

    @Override
    public void handle(Player player, Packet packet) {
        int slot = packet.getLEShortA() & 0xFF;
        int id = packet.getShort() & 0xFFFF;
        int interfaceId = packet.getLEInt();

        ItemDefinition itemId = ItemDefinition.forId(id);
        boolean membersItem = itemId.isMembersOnly();


        if (player.getAttribute("cutScene") != null) {
            return;
        }
        if (Constants.MEMBER_CHECK) {
            if (membersItem && !player.isMembers()) {
                player.getActionSender().sendMessage("Please go on the forums; You're not a member.");
                return;
            }
        }

        if (player.getInterfaceAttribute("fightPitOrbs") != null) {
            return;
        }

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "WieldItem", new Object[]{"ID: " + id, "Interface: " + interfaceId});

        switch (interfaceId) {
            case Inventory.INTERFACE:
                if (slot >= 0 && slot < Inventory.SIZE) {
                    if (player.getCombatState().isDead()) {
                        return;
                    }
                    if (player.isWerewolf()) {
                        player.getActionSender().sendMessage("You cannot do this as a werewolf.");
                        return;
                    }
                    Item item = player.getInventory().get(slot);
                    if (item != null && item.getId() == id) {
                        final Action action = new WieldItemAction(player, id, slot, 0);
                        if (player.getCombatState().getWeaponSwitchTimer() < 1) {
                            action.execute();
                        } else {
                            World.getWorld().submit(new Tickable(player.getCombatState().getWeaponSwitchTimer()) {
                                @Override
                                public void execute() {
                                    action.execute();
                                    this.stop();
                                }
                            });
//						player.getActionQueue().addAction(action);
                        }
                    }
                }
                break;
        }
    }

}
