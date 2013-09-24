package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class util {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static int maxLength = 105;

    public static void info(String msg) {
        log.info("[MCMEPVP] " + msg);
    }

    public static void warning(String msg) {
        log.warning("[MCMEPVP] " + msg);
    }

    public static void severe(String msg) {
        log.severe("[MCMEPVP] " + msg);
    }

    public static void debug(String msg) {
        if (config.debug) {
            util.info("DEBUG: " + msg);
        }
    }

    public static void notifyAdmin(String name, int action, String[] msg) {
        ChatColor desired;
        if (action == 1) {
            desired = MCMEPVP.positivecolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " (" + MCMEPVP.Participants + "/" + Bukkit.getOnlinePlayers().length + ")");
                }
            }
        }
        if (action == 2) {
            desired = MCMEPVP.negativecolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " (" + MCMEPVP.Participants + "/" + Bukkit.getOnlinePlayers().length + ")");
                }
            }
        }
        if (action == 3) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has changed the map to " + MCMEPVP.highlightcolor + MCMEPVP.PVPMap + ".");
                }
            }
        }
        if (action == 4) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has changed the game type to " + MCMEPVP.highlightcolor + MCMEPVP.PVPGT + ".");
                }
            }
        }
        if (action == 5) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has changed the required score for " + msg[0] + " to " + MCMEPVP.highlightcolor + msg[1] + ".");
                }
            }
        }
        if (action == 6) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has created a new map: " + MCMEPVP.highlightcolor + msg[0] + ".");
                }
            }
        }
        if (action == 7) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has removed the map: " + MCMEPVP.highlightcolor + msg[0] + ".");
                }
            }
        }
        if (action == 8) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has added flag #" + msg[1] + " to the map: " + MCMEPVP.highlightcolor + msg[0] + ".");
                }
            }
        }
        if (action == 9) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has unlocked the server.");
                }
            }
        }
        if (action == 10) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has locked the server.");
                }
            }
        }
        if (action == 11) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has force joined " + MCMEPVP.highlightcolor + msg[0] + " team.");
                }
            }
        }
        if (action == 12) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has enabled debug mode.");
                    p.sendMessage(MCMEPVP.highlightcolor + "Statistics will not be recorded.");
                }
            }
        }
        if (action == 13) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has disabled debug mode.");
                    p.sendMessage(MCMEPVP.highlightcolor + "Statistics will be recorded.");
                }
            }
        }
        if (action == 14) {
            desired = MCMEPVP.primarycolor;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("mcmepvp.admin")) {
                    p.sendMessage(desired + name + " has " + msg[0] + " auto-lobby mode.");
                }
            }
        }
    }
}
