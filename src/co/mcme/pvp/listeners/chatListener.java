package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class chatListener implements Listener {

    public chatListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        if (event.getMessage().startsWith("u00a")) {
            //player has WorldEdit CUI
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String Status = MCMEPVP.PlayerStatus.get(event.getPlayer().getName());
                String PlayerTeam = MCMEPVP.PlayerStatus.get(player.getName());
                String label = "[Jerk] ";
                if (Status.equals("red")) {
                    label = ChatColor.RED + "[Team Red] ";
                }
                if (Status.equals("blue")) {
                    label = ChatColor.BLUE + "[Team Blue] ";
                }
                if (Status.equals("spectator")) {
                    label = "[Spectator] ";
                }
                if (Status.equals("participant")) {
                    label = ChatColor.GREEN + "[Participant] ";
                }
                if (Status.equals("spectator") || Status.equals("participant") || PlayerTeam.equals(Status)) {
                    player.sendMessage(label + event.getPlayer().getName() + ": " + ChatColor.WHITE + event.getMessage());
                }
            }
        }
    }
}
