package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.teamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class chatListener implements Listener {

    public chatListener(MCMEPVP instance) {
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        if (MCMEPVP.adminChat.contains(event.getPlayer().getName())) {
        	String msg = event.getMessage();
			for (Player currentplayer : Bukkit.getOnlinePlayers()) {
				if (currentplayer.hasPermission("mcmepvp.adminchat")) {
					currentplayer.sendMessage(ChatColor.WHITE + "["
							+ MCMEPVP.admincolor + "A" + ChatColor.WHITE
							+ "] " + MCMEPVP.admincolor + event.getPlayer().getName()
							+ ": " + msg);
				}
			}
			System.out.print("[MCMEPVP] (adminchat) " + event.getPlayer().getName() + MCMEPVP.suff + event.getMessage());
			return;
        } else {
        	for (Player p : Bukkit.getOnlinePlayers()) {
                String senderTeam = teamUtil.getPlayerTeam(event.getPlayer());
                String receiverStatus = teamUtil.getPlayerTeam(p);
                String label = "";
                if (senderTeam.equals("fighter")) {
                    label = ChatColor.DARK_GREEN + "Fighter ";
                }
                if (senderTeam.equals("red")) {
                    label = ChatColor.RED + "Team Red ";
                }
                if (senderTeam.equals("blue")) {
                    label = ChatColor.BLUE + "Team Blue ";
                }
                if (senderTeam.equals("participant")) {
                    label = ChatColor.GREEN + "Participant ";
                }
                if (receiverStatus.equals("spectator") || receiverStatus.equals("participant") || receiverStatus.equals("fighter") || receiverStatus.equals(senderTeam)) {
                    p.sendMessage(label + event.getPlayer().getName() + ": " + ChatColor.WHITE + event.getMessage());
                }
            }
        }
    }
}
