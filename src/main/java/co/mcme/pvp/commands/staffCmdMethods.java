package co.mcme.pvp.commands;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.CurrentGame;
import static co.mcme.pvp.MCMEPVP.CurrentLobby;
import static co.mcme.pvp.MCMEPVP.GameStatus;
import static co.mcme.pvp.MCMEPVP.GameTypes;
import static co.mcme.pvp.MCMEPVP.Maps;
import static co.mcme.pvp.MCMEPVP.PVPGT;
import static co.mcme.pvp.MCMEPVP.PVPMap;
import static co.mcme.pvp.MCMEPVP.Participants;
import static co.mcme.pvp.MCMEPVP.locked;
import static co.mcme.pvp.MCMEPVP.resetGame;
import co.mcme.pvp.gametypes.teamConquestGame;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class staffCmdMethods {

    Plugin plugin;

    public staffCmdMethods(Plugin plug) {
        this.plugin = plug;
    }
    static ChatColor scd = ChatColor.GRAY;

    // ----------------//
    // PUBLIC METHODS
    // ----------------//
    // UNLOCK
    public static void pvpUnlock(Player p) {
        if (p.hasPermission("mcmepvp.unlock")) {
            locked = false;
            util.notifyAdmin(p.getName(), 9, null);
        } else {
            nope(p);
        }
    }

    // LOCK
    public static void pvpLock(Player p) {
        if (p.hasPermission("mcmepvp.lock")) {
            for (Player q : Bukkit.getOnlinePlayers()) {
                if (!q.hasPermission("mcmepvp.ignorelock")) {
                    q.kickPlayer("PVP is over! Come Back Soon!");
                }
            }
            locked = true;
            util.notifyAdmin(p.getName(), 10, null);
        } else {
            nope(p);
        }
    }

    // AUTO
    public static void pvpAuto(Player p) {
        if (p.hasPermission("mcmepvp.autorun")) {
            if (MCMEPVP.GameStatus == 0) {
                CurrentLobby.clearBoard();
                String[] msg = new String[1];
                if (MCMEPVP.autorun) {
                    MCMEPVP.autorun = false;
                    MCMEPVP.resetGame();
                    msg[0] = "disabled";
                } else {
                    MCMEPVP.autorun = true;
                    MCMEPVP.resetGame();
                    msg[0] = "enabled";
                }
                util.notifyAdmin(p.getName(), 14, msg);
            }
        } else {
            nope(p);
        }
    }

    // MAP
    public static void pvpMap(Player p, String map) {
        if (p.hasPermission("mcmepvp.map")) {
            if (GameStatus == 0) {
                boolean isValid = false;
                String newMap = PVPMap;

                for (String s : Maps) {
                    if (s.toLowerCase().contains(map.toLowerCase())) {
                        newMap = s;
                        isValid = true;
                        break;
                    }
                }

                if (isValid) {
                    if (!PVPMap.equals(newMap)) {
                        PVPMap = newMap;
                        util.notifyAdmin(p.getName(), 3, null);
                    } else {
                        p.sendMessage(MCMEPVP.positivecolor + newMap + scd
                                + " is already selected!");
                    }
                } else {
                    p.sendMessage(MCMEPVP.negativecolor + "'" + map
                            + "' is not a valid Map!");
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor
                        + "Can't change maps while a game is running!");
            }
        } else {
            nope(p);
        }
    }

    // GAMETYPE
    public static void pvpGameType(Player p, String gt) {
        if (p.hasPermission("mcmepvp.gt")) {
            if (GameStatus == 0) {
                boolean isValid = false;
                String newGt = PVPGT;

                for (String s : GameTypes) {
                    if (s.equalsIgnoreCase(gt)) {
                        newGt = s;
                        isValid = true;
                    }
                }

                if (isValid) {
                    if (!PVPGT.equals(newGt)) {
                        PVPGT = newGt;
                        util.notifyAdmin(p.getName(), 4, null);
                    } else {
                        p.sendMessage(MCMEPVP.positivecolor + newGt + scd
                                + " is already selected!");
                    }
                } else {
                    p.sendMessage(MCMEPVP.negativecolor + "'" + gt
                            + "' is not a valid GameType!");
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor
                        + "Can't change gametypes while a game is running!");
            }
        } else {
            nope(p);
        }
    }

    // SETSCORE
    public static void pvpSetScore(Player p, String score) {
        if (p.hasPermission("mcmepvp.setscore")) {
            if (isInt(score)) {
                boolean gameon = false;
                int i = Integer.valueOf(score);
                String gt = PVPGT;

                if (GameStatus == 1) {
                    gameon = true;
                }
                if (gt.equals("TSL")) {
                    if (gameon) {
                        MCMEPVP.CurrentGame.getObjective().setDisplayName(
                                "Score: " + i);
                        Bukkit.broadcastMessage(MCMEPVP.positivecolor
                                + "To win, you must now score "
                                + MCMEPVP.highlightcolor + score
                                + MCMEPVP.positivecolor + " points.");
                    }
                    String[] msg = new String[2];
                    msg[0] = "TSL";
                    msg[1] = score;
                    config.TSLscore = i;
                    MCMEPVP.inst().getConfig().set("score.TSL", i);
                    MCMEPVP.inst().saveConfig();

                    util.notifyAdmin(p.getName(), 5, msg);
                }
                if (gt.equals("TCQ")) {
                    if (gameon) {
                        teamConquestGame.setNewScore(i);
                        Bukkit.broadcastMessage(MCMEPVP.positivecolor
                                + "Team lives updated!");
                    }
                    String[] msg = new String[2];
                    msg[0] = "TCQ";
                    msg[1] = score;
                    config.TCQscore = i;
                    MCMEPVP.inst().getConfig().set("score.TCQ", i);
                    MCMEPVP.inst().saveConfig();

                    util.notifyAdmin(p.getName(), 5, msg);
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor + "/pvp setscore #score");
            }
        } else {
            nope(p);
        }
    }

    // START
    public static void pvpStart(Player p) {
        if (p.hasPermission("mcmepvp.start")) {
            if (GameStatus == 0) {
                if (Participants >= 2) {
                    MCMEPVP.startGame();
                } else {
                    p.sendMessage(MCMEPVP.negativecolor
                            + "There need to be at least two participants!");
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor + "Game already running!");
            }
        } else {
            nope(p);
        }
    }

    // STOP
    public static void pvpStop(Player p) {
        if (p.hasPermission("mcmepvp.stop")) {
            Bukkit.getServer().broadcastMessage(
                    MCMEPVP.highlightcolor
                    + "The PVP Event has been aborted by an admin!");
            resetGame();
        } else {
            nope(p);
        }
    }

    // FORCEJOIN
    public static void pvpForceJoin(Player p, String a) {
        if (p.hasPermission("mcmepvp.forcejoin")) {
            if (GameStatus == 1) {
                if (a.equalsIgnoreCase("blue") || a.equalsIgnoreCase("red")) {
                    CurrentGame.addTeam(p, a.toLowerCase());
                    util.notifyAdmin(p.getName(), 11, new String[]{a});
                } else {
                    p.sendMessage(MCMEPVP.negativecolor + a
                            + " team not recognised!");
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor
                        + "No game running, no team to join.");
            }
        } else {
            nope(p);
        }
    }

    // HORSEMODE
    public static void pvpHorse(Player p) {
        if (p.hasPermission("mcmepvp.horsemode")) {
            if (MCMEPVP.horseMode) {
                MCMEPVP.horseMode = false;
                p.sendMessage(ChatColor.GRAY + "Horse mode Disabled!");
            } else {
                MCMEPVP.horseMode = true;
                p.sendMessage(ChatColor.GRAY + "Horse mode Enabled!");
            }
        } else {
            nope(p);
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
