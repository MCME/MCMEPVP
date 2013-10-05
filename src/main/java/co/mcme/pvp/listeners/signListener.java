package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.gearGiver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class signListener implements Listener {

    // @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Sign s = (Sign) event.getBlock();
        String[] lines = s.getLines();
        int id = MCMEPVP.loot.size() + 1;
        if (player.hasPermission("mcmepvp.createlootsign") && lines[0].equalsIgnoreCase("[lootsign]")) {
            if (lines[1] != null) {
                String name = lines[1];
                Plugin main = Bukkit.getPluginManager().getPlugin("MCMEPVP");
                Configuration conf = main.getConfig();
                conf.set("loot." + id, name);
                main.saveConfig();
                MCMEPVP.loot.add(id, name);
                s.setLine(0, "§c[LootSign]");
                s.setLine(2, String.valueOf(id));
                s.update();
            }
        }
    }

    // @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST)) {
                Sign s = (Sign) event.getClickedBlock();
                String[] lines = s.getLines();
                if (lines[0].equals("§c[LootSign]")) {
                    gearGiver.rewardLoot(lines[1], p);
                }
            }
        }
    }
}
