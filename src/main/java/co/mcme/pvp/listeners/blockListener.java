package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.blockUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class blockListener implements Listener {

    blockUtil util = new blockUtil();
    public static HashMap<Location, List<Integer>> explodedBlocks = new HashMap<Location, List<Integer>>();
    public static List<Integer> explodeableList = new ArrayList<Integer>();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (MCMEPVP.GameStatus == 1) {
            if (MCMEPVP.CurrentGame.allowBlockBreak() || (p.hasPermission("mcmepvp.ignorebuildchecks") && p.getGameMode().equals(GameMode.CREATIVE))) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        } else {
            if (p.hasPermission("mcmepvp.ignorebuildchecks")) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (MCMEPVP.GameStatus == 1) {
            if (MCMEPVP.CurrentGame.allowBlockPlace() || (p.hasPermission("mcmepvp.ignorebuildchecks") && p.getGameMode().equals(GameMode.CREATIVE))) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        } else {
            if (p.hasPermission("mcmepvp.ignorebuildchecks")) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (util.isContainer(event.getClickedBlock())) {
                if (MCMEPVP.GameStatus == 1) {
                    if (MCMEPVP.CurrentGame.allowContainerIteraction() || (p.hasPermission("mcmepvp.ignorebuildchecks") && p.getGameMode().equals(GameMode.CREATIVE))) {
                        event.setCancelled(false);
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    if (p.hasPermission("mcmepvp.ignorebuildchecks")) {
                        event.setCancelled(false);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void primed(ExplosionPrimeEvent event) {
        if (MCMEPVP.CurrentGame.allowExplosionLogging()) {
            event.setCancelled(true);
            World w = event.getEntity().getWorld();
            Location l = event.getEntity().getLocation();
            w.createExplosion(l, 20);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void explo(EntityExplodeEvent event) {
        if (MCMEPVP.CurrentGame.allowExplosionLogging()) {
            event.setYield(0);
            for (Block b : event.blockList()) {
                int type = b.getTypeId();
                int data = b.getData();
                if (type != 0 && type != 46 && type != 69 && type != 76) {
                    List<Integer> block = new ArrayList<Integer>();
                    block.add(0, type);
                    block.add(1, data);
                    World w = b.getWorld();
                    Location l = b.getLocation();
                    explodedBlocks.put(l, block);
                    w.playEffect(l, Effect.SMOKE, 9);
                }
            }
            derp();
        } else {
            event.setCancelled(true);
        }
    }

    public static void derp() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
            @Override
            public void run() {
                for (Location l : explodedBlocks.keySet()) {
                    List<Integer> block = explodedBlocks.get(l);
                    if (!explodeableList.contains(block.get(0))) {
                        l.getBlock().setTypeIdAndData(block.get(0),
                                block.get(1).byteValue(), true);
                    }
                }

            }
        }, 1);
    }
}
