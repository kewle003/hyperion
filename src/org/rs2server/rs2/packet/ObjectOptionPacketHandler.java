package org.rs2server.rs2.packet;

import org.rs2server.rs2.ScriptManager;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Cannon;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Door;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GameObjectDefinition;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.NPC;
import org.rs2server.rs2.model.Player;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.model.quests.QuestRepository;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.*;
import org.rs2server.rs2.model.skills.Agility.Obstacle;
import org.rs2server.rs2.model.skills.Mining.Rock;
import org.rs2server.rs2.model.skills.Smithing.Bar;
import org.rs2server.rs2.model.skills.Thieving.Stall;
import org.rs2server.rs2.model.skills.Woodcutting.Tree;
import org.rs2server.rs2.net.Packet;

/**
 * Object option packet handler.
 *
 * @author Graham Edgecombe
 */
public class ObjectOptionPacketHandler implements PacketHandler {

    private static final int OPTION_1 = 31, OPTION_2 = 203, ITEM_ON_OBJECT = 134;

    @Override
    public void handle(Player player, Packet packet) {
        if (player.getAttribute("busy") != null) {
            return;
        }
        player.getActionQueue().clearRemovableActions();
        switch (packet.getOpcode()) {
            case OPTION_1:
                handleOption1(player, packet);
                break;
            case OPTION_2:
                handleOption2(player, packet);
                break;
            case ITEM_ON_OBJECT:
                handleOptionItem(player, packet);
                break;
        }
    }

    /**
     * Handles the option 1 packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOption1(final Player player, Packet packet) {
        final int x = packet.getLEShortA();
        final int id = packet.getShort();
        final int y = packet.getShort();
        int z = player.getLocation().getZ();
        System.out.println("ID" + id);
        /*if(player.getAttribute("temporaryHeight") != null) {
              z = player.getAttribute("temporaryHeight");
          } */
        final Location loc = Location.create(x, y, z);

        Region r = player.getRegion();
        final GameObject obj = r.getGameObject(loc, id);
        if (obj == null && id != 23271) {
            return;
        }

        GameObjectDefinition def = GameObjectDefinition.forId(id);
        int width = 1;
        int height = 1;
        if (def != null) {
            width = def.getSizeX();
            height = def.getSizeY();
        }

        if (obj != null && id != 2302) {
            System.out.println("Loc " + loc + "     obj " + id + "     reg " + r.getCoordinates().toString());
            player.face(player.getLocation().oppositeTileOfEntity(obj));
            player.getActionSender().sendDebugPacket(packet.getOpcode(), "ObjOpt1", new Object[]{"ID: " + id, "Loc: " + loc, "X: " + obj.getWidth(), "Y: " + obj.getHeight(), "Direction: " + obj.getDirection(), "Type: " + obj.getType()});
        }
        int distance = 1;

        if (id == 2282) {
            distance = 5;
        }
        Action action = null;
        Tree tree = Tree.forId(id);
        Rock rock = Rock.forId(id);
        final Obstacle obstacle = Obstacle.forLocation(loc);
        final Door door = r.doorForLocation(loc, id);
        if (tree != null) {
            action = new Woodcutting(player, obj);
        } else if (rock != null) {
            action = new Mining(player, obj);
        } else if (obstacle != null) {
            action = new Action(player, 0) {
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
                    this.stop();
                    Agility.tackleObstacle(player, obstacle, obj);
                }
            };
        } else {
            action = new Action(player, 0) {
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
                    this.stop();
                    if (door != null) {
                        door.open(true);
                    } else {
                        switch (id) {
                            case 23271:
                                Agility.tackleObstacle(player, Obstacle.WILDERNESS_DITCH, obj);
                                break;
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                                if (player.getAttribute("cannon") != null) {
                                    Cannon cannon = (Cannon) player.getAttribute("cannon");
                                    if (cannon.getGameObject().getLocation().equals(loc)) {
                                        if (id == 6) {
                                            cannon.fire();
                                        } else {
                                            cannon.destroy();
                                        }
                                    } else {
                                        player.getActionSender().sendMessage("This is not your cannon.");
                                    }
                                } else {
                                    player.getActionSender().sendMessage("This is not your cannon.");
                                }
                                break;
                            case 450:
                            case 451:
                            case 452:
                            case 453:
                                player.getActionSender().sendMessage("There is no ore currently available in this rock.");
                                return;
                            case 26384:
                                if (player.getSkills().getLevel(Skills.STRENGTH) < 70) {
                                    player.getActionSender().sendMessage("You need a Strength level of 70 to bang this door down.");
                                } else if (player.getInventory().getCount(2347) < 1) {
                                    player.getActionSender().sendMessage("You need a hammer to bang this door down.");
                                } else {
                                    player.getActionQueue().addAction(new Action(player, 3) {
                                        @Override
                                        public void execute() {
                                            if (player.getLocation().getX() == 2851) {
                                                player.setTeleportTarget(Location.create(player.getLocation().getX() - 1, player.getLocation().getY(), player.getLocation().getZ()));
                                            } else if (player.getLocation().getX() == 2850) {
                                                player.setTeleportTarget(Location.create(player.getLocation().getX() + 1, player.getLocation().getY(), player.getLocation().getZ()));
                                            }
                                            this.stop();
                                        }

                                        @Override
                                        public AnimationPolicy getAnimationPolicy() {
                                            return AnimationPolicy.RESET_NONE;
                                        }

                                        @Override
                                        public CancelPolicy getCancelPolicy() {
                                            return CancelPolicy.ALWAYS;
                                        }

                                        @Override
                                        public StackPolicy getStackPolicy() {
                                            return StackPolicy.NEVER;
                                        }
                                    });
                                    player.playAnimation(Animation.create(7002));
                                }
                                break;
                            default:
                                if (obj.getDefinition() != null) {
                                    if (obj.getDefinition().getName().toLowerCase().contains("banana")) {
                                        player.getActionSender().sendMessage("You reach out to the tree...");
                                        player.getActionQueue().addAction(new Action(player, 3) {
                                            @Override
                                            public void execute() {
                                                player.getActionSender().sendMessage("...and grab a banana.");
                                                player.getInventory().add(new Item(1963));
                                                this.stop();
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
                                        });
                                        return;
                                    } else if (obj.getDefinition().getName().toLowerCase().contains("bank")) {
                                        NPC closestBanker = null;
                                        int closestDist = 10;
                                        for (NPC banker : World.getWorld().getRegionManager().getLocalNpcs(player)) {
                                            if (banker.getDefinition().getName().toLowerCase().contains("banker")) {
                                                if (obj.getLocation().distanceToPoint(banker.getLocation()) < closestDist) {
                                                    closestDist = obj.getLocation().distanceToPoint(banker.getLocation());
                                                    closestBanker = banker;
                                                }
                                            }
                                        }
                                        if (closestBanker != null) {
                                            player.setInteractingEntity(InteractionMode.TALK, closestBanker);
                                            closestBanker.setInteractingEntity(InteractionMode.TALK, player);
                                            DialogueManager.openDialogue(player, 0);
                                        }
                                        return;
                                    }
                                }
                                String scriptName = "objectOptionOne" + id;
                                if (!ScriptManager.getScriptManager().invokeWithFailTest(scriptName, player, obj)) {
                                    player.getActionSender().sendMessage("Nothing interesting happens.");
                                }
                                break;
                        }
                    }
                }
            };
        }
        if (action != null) {
            player.addCoordinateAction(player.getWidth(), player.getHeight(), loc, width, height, distance, action);
        }
    }

    /**
     * Handles the option 2 packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOption2(final Player player, Packet packet) {
        final int x = packet.getShort() & 0xFFFF;
        final int id = packet.getLEShort() & 0xFFFF;
        final int y = packet.getShortA() & 0xFFFF;
        int z = player.getLocation().getZ();
        /*	if(player.getAttribute("temporaryHeight") != null) {
              z = player.getAttribute("temporaryHeight");
          } */
        final Location loc = Location.create(x, y, z);

        final GameObject obj = player.getRegion().getGameObject(loc, id);
        if (obj == null) {
            return;
        }
        player.face(player.getLocation().oppositeTileOfEntity(obj));

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ObjOpt2", new Object[]{"ID: " + id, "Loc: " + loc});

        Action action = null;
        final Stall stall = Stall.forId(id);
        if (stall != null) {
            player.getActionQueue().addAction(new Thieving(player, obj));
            return;
        }

        Action prospectAction = null;
        final Rock rock = Rock.forId(id);
        if (rock != null || (id == 450 || id == 451 || id == 452 || id == 453)) {
            prospectAction = new Action(player, 4) {
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
                    if (id == 450 || id == 451 || id == 452 || id == 453) {
                        player.getActionSender().sendMessage("This rock has no current ore available.");
                    } else {
                        player.getActionSender().sendMessage("This rock contains " + ItemDefinition.forId(rock.getOreId()).getName().toLowerCase() + ".");
                    }
                    this.stop();
                }
            };
        }
        final Action finalProspectAction = prospectAction;
        action = new Action(player, 0) {
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
                if (rock != null && finalProspectAction != null) {
                    player.getActionSender().sendMessage("You examine the rock for ores...");
                    player.getActionQueue().addAction(finalProspectAction);
                } else {
                    switch (id) {
                        case 6:
                            if (player.getAttribute("cannon") != null) {
                                Cannon cannon = (Cannon) player.getAttribute("cannon");
                                if (loc.equals(cannon.getGameObject().getLocation())) {
                                    cannon.destroy();
                                }
                            }
                            break;
                        default:
                            String scriptName = "objectOptionTwo" + id;
                            if (!ScriptManager.getScriptManager().invokeWithFailTest(scriptName, player, obj)) {
                                player.getActionSender().sendMessage("Nothing interesting happens.");
                            }
                            break;
                    }
                }
                this.stop();
            }
        };
        if (action != null) {
            player.addCoordinateAction(player.getWidth(), player.getHeight(), obj.getLocation(), obj.getWidth(), obj.getHeight(), 1, action);
        }
    }

    /**
     * Handles the item on object packet.
     *
     * @param player The player.
     * @param packet The packet.
     */
    private void handleOptionItem(final Player player, Packet packet) {
        final int x = packet.getShortA() & 0xFFFF;
        final int slot = packet.getShortA() & 0xFFFF;
        final int id = packet.getLEShort() & 0xFFFF;
        final int y = packet.getLEShortA() & 0xFFFF;
        int z = player.getLocation().getZ();
        /*	if(player.getAttribute("temporaryHeight") != null) {
              z = player.getAttribute("temporaryHeight");
          } */
        final Location loc = Location.create(x, y, z);

        final Item item = player.getInventory().get(slot);
        if (item == null) {
            return;
        }

        final GameObject obj = player.getRegion().getGameObject(loc, id);
        if (obj == null) {
            return;
        }
        player.face(player.getLocation().oppositeTileOfEntity(obj));

        player.getActionSender().sendDebugPacket(packet.getOpcode(), "ItemOnObject", new Object[]{"ID: " + id, "Loc: " + loc});

        Action action = null;
        action = new Action(player, 0) {
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
                if (obj.getDefinition().getName().equalsIgnoreCase("Anvil")) {
                    Bar bar = Bar.forId(item.getId());
                    if (bar != null) {
                        Smithing.openSmithingInterface(player, bar);
                    }
                } else {
                    switch (id) {
                        case 6:
                            if (player.getAttribute("cannon") != null) {
                                Cannon cannon = (Cannon) player.getAttribute("cannon");
                                if (loc.equals(cannon.getGameObject().getLocation())) {
                                    if (item.getId() == 2) {
                                        int cannonBalls = cannon.getCannonBalls();
                                        if (cannonBalls >= 30) {
                                            player.getActionSender().sendMessage("Your cannon is already full.");
                                            return;
                                        }
                                        int newCannonBalls = item.getCount();
                                        if (newCannonBalls > 30) {
                                            newCannonBalls = 30;
                                        }
                                        if (newCannonBalls + cannonBalls > 30) {
                                            newCannonBalls = 30 - cannonBalls;
                                        }
                                        if (newCannonBalls < 1) {
                                            return;
                                        }
                                        player.getInventory().remove(new Item(2, newCannonBalls));
                                        cannon.addCannonBalls(newCannonBalls);
                                        player.getActionSender().sendMessage("You load " + newCannonBalls + " cannonball" + (newCannonBalls > 1 ? "s" : "") + " into your cannon.");
                                    }
                                }
                            }
                            break;
                        case 7:
                            if (player.getAttribute("cannon") != null) {
                                Cannon cannon = (Cannon) player.getAttribute("cannon");
                                if (loc.equals(cannon.getGameObject().getLocation())) {
                                    if (item.getId() == 8) {
                                        cannon.addPart(new Item(8, 1));
                                    }
                                }
                            }
                            break;
                        case 8:
                            if (player.getAttribute("cannon") != null) {
                                Cannon cannon = (Cannon) player.getAttribute("cannon");
                                if (loc.equals(cannon.getGameObject().getLocation())) {
                                    if (item.getId() == 10) {
                                        cannon.addPart(new Item(10, 1));
                                    }
                                }
                            }
                            break;
                        case 9:
                            if (player.getAttribute("cannon") != null) {
                                Cannon cannon = (Cannon) player.getAttribute("cannon");
                                if (loc.equals(cannon.getGameObject().getLocation())) {
                                    if (item.getId() == 12) {
                                        cannon.addPart(new Item(12, 1));
                                    }
                                }
                            }
                            break;
                    }
                }
                this.stop();
            }
        };
        if (action != null) {
            player.addCoordinateAction(player.getWidth(), player.getHeight(), obj.getLocation(), obj.getWidth(), obj.getHeight(), 1, action);
        }
    }


}
