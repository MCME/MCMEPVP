package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.PVPGT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class flagListener implements Listener {

    public static int redFlagCount = 0;
    public static int blueFlagCount = 0;
    public static HashMap<Integer, String> Flags = new HashMap<Integer, String>();
    public static HashMap<Integer, List<Block>> BlockFlagMarkers = new HashMap<Integer, List<Block>>();
    public static HashMap<Integer, List<Block>> CarpetFlagMarkers = new HashMap<Integer, List<Block>>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && event.getClickedBlock().getType() == Material.BEACON) {

            Player player = event.getPlayer();
            String Status = MCMEPVP.getPlayerTeam(player);
            int flagData = event.getClickedBlock().getData();

            if (PVPGT.equals("TCQ")) {
                if (Status.equals("red")) {
                    if (Flags.containsKey(flagData)) {
                        if (Flags.get(flagData).contains(Status)) {
                            player.sendMessage("Your team has already captured this flag!");
                        } else {
                            if (Flags.get(flagData).contains("blue")) {
                                Flags.put(flagData, "red");
                                redFlagCount++;
                                blueFlagCount--;
                                flagUpdate(event);
                                //
                                co.mcme.pvp.gametypes.teamConquestGame.onFlagUpdate();
                                //
                                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE
                                        + " stole " + ChatColor.YELLOW + "Flag " + flagData + ChatColor.WHITE
                                        + " from the " + ChatColor.BLUE + "Blues!");
                            }
                        }
                    } else {
                        Flags.put(flagData, "red");
                        redFlagCount++;
                        flagUpdate(event);
                        //
                        co.mcme.pvp.gametypes.teamConquestGame.onFlagUpdate();
                        //
                        Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.WHITE
                                + " captured " + ChatColor.YELLOW + "Flag " + flagData + ChatColor.WHITE + "!");
                    }
                }
                if (Status.equals("blue")) {
                    if (Flags.containsKey(flagData)) {
                        if (Flags.get(flagData).contains(Status)) {
                            player.sendMessage("Your team has already captured this flag!");
                        } else {
                            if (Flags.get(flagData).contains("red")) {
                                Flags.put(flagData, "blue");
                                blueFlagCount++;
                                redFlagCount--;
                                flagUpdate(event);
                                //
                                co.mcme.pvp.gametypes.teamConquestGame.onFlagUpdate();
                                //
                                Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + ChatColor.WHITE
                                        + " stole " + ChatColor.YELLOW + "Flag " + flagData + ChatColor.WHITE
                                        + " from the " + ChatColor.RED + "Reds!");
                            }
                        }
                    } else {
                        Flags.put(flagData, "blue");
                        blueFlagCount++;
                        flagUpdate(event);
                        //
                        co.mcme.pvp.gametypes.teamConquestGame.onFlagUpdate();
                        //
                        Bukkit.broadcastMessage(ChatColor.BLUE + player.getName() + ChatColor.WHITE
                                + " captured " + ChatColor.YELLOW + "Flag " + flagData + ChatColor.WHITE + "!");
                    }
                }
            }
            // Prevents players accidentally getting stuck in the beacon inventory
            event.setCancelled(true);
        }
        return;
    }

    private void flagUpdate(PlayerInteractEvent event) {
        List<Block> list = new ArrayList<Block>();
        List<Block> clist = new ArrayList<Block>();
        int flagData = event.getClickedBlock().getData();
        int i = 0;

        Player player = event.getPlayer();
        String Status = MCMEPVP.getPlayerTeam(player);

        if (Status.equals("red")) {
            i = 14;
        }
        if (Status.equals("blue")) {
            i = 11;
        }
        Location bloc = event.getClickedBlock().getLocation();
        World w = bloc.getWorld();
        bloc.setY(bloc.getY() + 40);
        bloc.setX(bloc.getX() + 1);
        Block b0 = w.getBlockAt(bloc);
        Location cloc = event.getClickedBlock().getLocation();
        cloc.setY(cloc.getY() + 1);
        cloc.setX(cloc.getX() + 1);
        Block c0 = w.getBlockAt(cloc);
        if (b0.getType() == Material.AIR
                || b0.getType() == Material.WOOL) {
            b0.setTypeIdAndData(35, (byte) i, true);
            list.add(b0);
        }
        if (c0.getType() == Material.AIR
                || c0.getType() == Material.CARPET) {
            c0.setTypeIdAndData(Material.CARPET.getId(), (byte) i, true);
            clist.add(c0);
        }


        Location bloc1 = event.getClickedBlock().getLocation();
        bloc1.setY(bloc1.getY() + 40);
        bloc1.setX(bloc1.getX() - 1);
        Block b1 = w.getBlockAt(bloc1);
        Location cloc1 = event.getClickedBlock().getLocation();
        cloc1.setY(cloc1.getY() + 1);
        cloc1.setX(cloc1.getX() - 1);
        Block c1 = w.getBlockAt(cloc1);
        if (b1.getType() == Material.AIR
                || b1.getType() == Material.WOOL) {
            b1.setTypeIdAndData(35, (byte) i, true);
            list.add(b1);
        }
        if (c1.getType() == Material.AIR
                || c1.getType() == Material.CARPET) {
            c1.setTypeIdAndData(Material.CARPET.getId(), (byte) i, true);
            clist.add(c1);
        }

        Location bloc2 = event.getClickedBlock().getLocation();
        bloc2.setY(bloc2.getY() + 40);
        bloc2.setZ(bloc2.getZ() + 1);
        Block b2 = w.getBlockAt(bloc2);
        Location cloc2 = event.getClickedBlock().getLocation();
        cloc2.setY(cloc2.getY() + 1);
        cloc2.setZ(cloc2.getZ() + 1);
        Block c2 = w.getBlockAt(cloc2);
        if (b2.getType() == Material.AIR
                || b2.getType() == Material.WOOL) {
            b2.setTypeIdAndData(35, (byte) i, true);
            list.add(b2);
        }
        if (c2.getType() == Material.AIR
                || c2.getType() == Material.CARPET) {
            c2.setTypeIdAndData(Material.CARPET.getId(), (byte) i, true);
            clist.add(c2);
        }

        Location bloc3 = event.getClickedBlock().getLocation();
        bloc3.setY(bloc3.getY() + 40);
        bloc3.setZ(bloc3.getZ() - 1);
        Block b3 = w.getBlockAt(bloc3);
        Location cloc3 = event.getClickedBlock().getLocation();
        cloc3.setY(cloc3.getY() + 1);
        cloc3.setZ(cloc3.getZ() - 1);
        Block c3 = w.getBlockAt(cloc3);
        if (b3.getType() == Material.AIR
                || b2.getType() == Material.WOOL) {
            b3.setTypeIdAndData(35, (byte) i, true);
            list.add(b3);
        }
        if (c3.getType() == Material.AIR
                || c3.getType() == Material.CARPET) {
            c3.setTypeIdAndData(Material.CARPET.getId(), (byte) i, true);
            clist.add(c3);
        }
        Location loc4 = event.getClickedBlock().getLocation();
        loc4.setY(loc4.getY() - 1);
        Block b4 = w.getBlockAt(loc4);
        b4.setTypeIdAndData(42, (byte) 0, true);
        list.add(b4);
        if (list.size() > 0) {
            BlockFlagMarkers.put(flagData, list);
        }
        if (clist.size() > 0) {
            CarpetFlagMarkers.put(flagData, clist);
        }
    }
}
