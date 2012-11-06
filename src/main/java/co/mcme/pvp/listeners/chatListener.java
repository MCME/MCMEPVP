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
    void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            String SenderStatus = MCMEPVP.PlayerStatus.get(event.getPlayer().getName());
            String ReceiverStatus = MCMEPVP.PlayerStatus.get(player.getName());
            String label = "";
            if (SenderStatus.equals("fighter")) {
                label = ChatColor.DARK_GREEN + "Fighter ";
            }
            if (SenderStatus.equals("red")) {
                label = ChatColor.RED + "Team Red ";
            }
            if (SenderStatus.equals("blue")) {
                label = ChatColor.BLUE + "Team Blue ";
            }
            if (SenderStatus.equals("participant")) {
                label = ChatColor.GREEN + "Participant ";
            }
            if (ReceiverStatus.equals("spectator") || ReceiverStatus.equals("participant") || ReceiverStatus.equals("fighter") || ReceiverStatus.equals(SenderStatus)) {
                player.sendMessage(label + event.getPlayer().getName() + ": " + ChatColor.WHITE + event.getMessage());
            }
        }
    }
}
