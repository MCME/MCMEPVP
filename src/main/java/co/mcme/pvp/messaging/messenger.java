package co.mcme.pvp.messaging;

import org.bukkit.ChatColor;

public class messenger {

    public void announceGameStart(String gt, boolean midgamejoins, String runner) {
        StringBuilder out = new StringBuilder();
        if (midgamejoins) {
            out.append(ChatColor.AQUA).append(runner).append(ChatColor.GRAY).append(" has started a `").append(ChatColor.AQUA).append(gt).append(ChatColor.GRAY).append("` game on the pvp server!");
        }
    }

    public void announceUnlock(String unlocker) {
        StringBuilder out = new StringBuilder();
        out.append(ChatColor.AQUA).append(unlocker).append(ChatColor.GRAY).append(" has just opened the pvp server!");
    }

    public void announceLock(String locker) {
        StringBuilder out = new StringBuilder();
        out.append(ChatColor.AQUA).append(locker).append(ChatColor.GRAY).append(" has just closed the pvp server!");
    }

    public void announceGameEnd(String gt, String winning) {
        StringBuilder out = new StringBuilder();
        out.append(ChatColor.GRAY).append("Team `").append(ChatColor.AQUA).append(winning).append(ChatColor.GRAY).append("` has just won a `").append(ChatColor.AQUA).append(gt).append(ChatColor.GRAY).append("` game on the pvp server!");
    }

    public void announce(String msg) {
        // open socket and write msg
    }
}
