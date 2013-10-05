package co.mcme.pvp.commands;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.PVPMap;
import co.mcme.pvp.util.util;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class devCmdMethods {

    public static JavaPlugin instance = (JavaPlugin) MCMEPVP.inst();
    static ChatColor scd = ChatColor.GRAY;

    // ----------------//
    // PUBLIC METHODS
    // ----------------//
    //ADD MAP
    @SuppressWarnings("unchecked")
    public static void pvpAdd(Player p, String a) {
        if (p.hasPermission("mcmepvp.add")) {

            if (instance.getConfig().getStringList("maps").contains(a)) {
                p.sendMessage(scd + "Map already exists!");
            } else {
                ((List<String>) instance.getConfig().getList("maps")).add(a);
                instance.saveConfig();

                PVPMap = a;
                util.notifyAdmin(p.getName(), 6, new String[]{a});
                util.notifyAdmin(p.getName(), 3, null);
            }
        } else {
            nope(p);
        }
    }
    //REMOVE MAP

    @SuppressWarnings("unchecked")
    public static void pvpRemove(Player p, String a) {
        if (p.hasPermission("mcmepvp.remove")) {
            if (instance.getConfig().getStringList("maps").contains(a)) {
                ((List<String>) instance.getConfig().getList("maps")).remove(a);
                instance.saveConfig();

                util.notifyAdmin(p.getName(), 7, new String[]{a});
            } else {
                p.sendMessage(scd + "Map doesn't exist!");
            }
        } else {
            nope(p);
        }
    }
    //SET MAP SPAWNS

    public static void pvpSetSpawn(Player p, String a) {
        if (p.hasPermission("mcmepvp.set")) {
            Vector loc = p.getLocation().toVector();

            String team = a.toLowerCase();
            String map = PVPMap.toLowerCase();
            if (team.equals("blue") || team.equals("red")
                    || team.equals("green") || team.equals("purple")
                    || team.equals("spectator")) {
                instance.getConfig().set(map + "." + team, loc);
                instance.saveConfig();

                p.sendMessage(MCMEPVP.primarycolor
                        + "Saved your Location as Team " + team
                        + "'s Spawn for Map '" + PVPMap + "'!");
            } else {
                p.sendMessage(scd + "That team name is not supported!");
            }
        } else {
            nope(p);
        }
    }
    //SET FLAG POINTS

    public static void pvpSetFlag(Player p, String a) {
        if (p.hasPermission("mcmepvp.set")) {
            if (isInt(a)) {
                int i = Integer.valueOf(a);
                if (i >= 0 || i <= 5) {
                    Vector loc = p.getLocation().toVector();
                    String[] msg = new String[2];
                    msg[0] = PVPMap;
                    msg[1] = String.valueOf(i);
                    loc.setY(loc.getY() - 2);

                    instance.getConfig().set(
                            PVPMap.toLowerCase() + ".Flag" + i, loc);
                    instance.saveConfig();

                    util.notifyAdmin(p.getName(), 8, msg);
                } else {
                    p.sendMessage(scd + "Flags must range between 0 and 5");
                }
            } else {
                p.sendMessage(scd + a + " is not an Integer!");
            }
        } else {
            nope(p);
        }
    }
    //SET REGION

    public static void pvpSetRegion(Player p, String a) {
        if (p.hasPermission("mcmepvp.set")) {
            if (a.equalsIgnoreCase("Eriador")
                    || a.equalsIgnoreCase("Rohan")
                    || a.equalsIgnoreCase("Lothlorien")
                    || a.equalsIgnoreCase("Gondor")) {
                instance.getConfig().set(PVPMap.toLowerCase() + ".region", a.toLowerCase());
                instance.saveConfig();

                p.sendMessage(MCMEPVP.primarycolor + "Saved region " + a + " for " + PVPMap + "'!");
            } else {
                p.sendMessage(scd + a + " is not a valid region!");
            }
        } else {
            nope(p);
        }
    }

    public static void pvpDebug(Player p) {
        if (p.hasPermission("mcmepvp.setdebug")) {
            if (MCMEPVP.debug) {
                MCMEPVP.debug = false;
                util.notifyAdmin(p.getName(), 13, null);
            } else {
                MCMEPVP.debug = true;
                util.notifyAdmin(p.getName(), 12, null);
            }
        }
    }

    // ----------------//
    // UTILITIES
    // ----------------//
    // Check if string is integer
    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // No perms message
    private static void nope(Player p) {
        p.sendMessage(MCMEPVP.negativecolor + "Sorry, you don't have permissions to use that!");
    }
}
