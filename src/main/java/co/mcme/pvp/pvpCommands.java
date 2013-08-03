package co.mcme.pvp;

import static co.mcme.pvp.MCMEPVP.CurrentGame;
import static co.mcme.pvp.MCMEPVP.GameStatus;
import static co.mcme.pvp.MCMEPVP.GameTypes;
import static co.mcme.pvp.MCMEPVP.Maps;
import static co.mcme.pvp.MCMEPVP.PVPGT;
import static co.mcme.pvp.MCMEPVP.PVPMap;
import static co.mcme.pvp.MCMEPVP.Participants;
import static co.mcme.pvp.MCMEPVP.getPlayerTeam;
import static co.mcme.pvp.MCMEPVP.isOnTeam;
import static co.mcme.pvp.MCMEPVP.locked;
import static co.mcme.pvp.MCMEPVP.queuePlayer;
import static co.mcme.pvp.MCMEPVP.resetGame;
import static co.mcme.pvp.MCMEPVP.unQueuePlayer;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class pvpCommands implements CommandExecutor {

    Plugin p;

    public pvpCommands(Plugin plug) {
        this.p = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        Player player = sender.getServer().getPlayer(sender.getName());
        if (sender instanceof Player) {
            if (label.equalsIgnoreCase("pvp")) {
                if (args.length != 0) {
                    String method = args[0];
                    if (method.equalsIgnoreCase("debug")) {
                        if (player.hasPermission("mcmepvp.setdebug")) {
                            if (MCMEPVP.debug) {
                                MCMEPVP.debug = false;
                                util.notifyAdmin(player.getName(), 13, null);
                                return true;
                            } else {
                                MCMEPVP.debug = true;
                                util.notifyAdmin(player.getName(), 12, null);
                                return true;
                            }
                        }
                    }
                    if (method.equalsIgnoreCase("join")) {
                        if (player.hasPermission("mcmepvp.join")) {
                            queuePlayer(player);
                            return true;
                        }
                    }
                    if (method.equalsIgnoreCase("leave")) {
                        if (player.hasPermission("mcmepvp.leave")) {
                            if (!isOnTeam(player) && GameStatus == 0) {
                                unQueuePlayer(player);
                                return true;
                            } else if (GameStatus == 1) {
                                player.sendMessage(MCMEPVP.negativecolor
                                        + "You cannot leave a game that is already running!");
                                return true;
                            }
                        }
                    }
                    if (method.equalsIgnoreCase("version")) {
                        if (player.hasPermission("mcmepvp.version")) {
                            player.sendMessage(MCMEPVP.primarycolor + "Currently running version: " + MCMEPVP.highlightcolor + Bukkit.getServer().getPluginManager().getPlugin("MCMEPVP").getDescription().getVersion());
                        }
                    }
                    if (method.equalsIgnoreCase("pitchyaw")) {
                        if (player.hasPermission("mcmepvp.checkpitchyaw")){
                            player.sendMessage("Pitch: " + player.getLocation().getPitch() + ", Yaw: " + player.getLocation().getYaw());
                        }
                    }
                    if (method.equalsIgnoreCase("remind")) {
                        if (player.hasPermission("mcmepvp.remind")) {
                            if (args[1] != null) {
                                if (args[1].equals("stats")) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.sendMessage(MCMEPVP.primarycolor + "Don't forget to check your stats at " + MCMEPVP.highlightcolor + "mcme.co/pvp/stats/" + p.getName());
                                    }
                                }
                                if (args[1].equals("join")) {
                                    ArrayList<String> notjoined = new ArrayList<String>();
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (!MCMEPVP.isQueued(p) && !MCMEPVP.isOnTeam(p)) {
                                            p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 100, 100);
                                            p.sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_RED + "You have not joined the game yet!");
                                            notjoined.add(p.getName());
                                        }
                                    }
                                    StringBuilder out = new StringBuilder();
                                    for (String name : notjoined){
                                        out.append(ChatColor.RED).append(name).append("\n");
                                    }
                                    player.playSound(player.getLocation(), Sound.BURP, 100, 100);
                                    player.sendMessage(out.toString());
                                }
                            }


                        }
                    }
                    if (method.equalsIgnoreCase("start")) {
                        if (player.hasPermission("mcmepvp.start")) {
                            if (GameStatus == 0) {
                                if (Participants >= 2) {
                                    MCMEPVP.startGame();
                                    return true;
                                } else {
                                    player.sendMessage(MCMEPVP.negativecolor
                                            + "There need to be at least two participants!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(MCMEPVP.negativecolor
                                        + "Game already running!");
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }
                    }
                    //DERP
                    if (method.equalsIgnoreCase("derp")) {
                        if (player.hasPermission("mcmepvp.derp")) {
                            if (args.length > 1) {
                                int score = 0;
                                if (args[1].contains("m")) {
                                    // RandomMap
                                    int maxMaps = Maps.size() - 1;
                                    int ranMap = getRandom(0, maxMaps);
                                    PVPMap = Maps.get(ranMap);
                                }
                                if (args[1].contains("g")) {
                                    // RandomGT
                                    int maxGt = GameTypes.size() - 1;
                                    int ranGt = getRandom(0, maxGt);
                                    PVPGT = GameTypes.get(ranGt);
                                }
                                if (args[1].contains("s")) {
                                    if (PVPGT.contains("TSL")) {
                                        int i = setScore(MCMEPVP.queue.size());
                                        config.TSLscore = i;
                                        p.saveConfig();
                                        String[] msg = new String[2];
                                        msg[0] = "TSL";
                                        msg[1] = String.valueOf(i);
                                    }
                                    if (PVPGT.contains("TCQ")) {
                                        int i = setScore(MCMEPVP.queue.size());
                                        config.TCQscore = i * 2;
                                        p.saveConfig();
                                        String[] msg = new String[2];
                                        msg[0] = "TCQ";
                                        msg[1] = String.valueOf(i * 2);
                                    }
                                }
                                if (PVPGT.equals("TSL")) {
                                    score = config.TSLscore;
                                }
                                if (PVPGT.equals("TCQ")) {
                                    score = config.TCQscore;
                                }
                                player.sendMessage(ChatColor.GRAY + "-------------------");
                                player.sendMessage(ChatColor.GRAY + "Map: "
                                        + ChatColor.AQUA + PVPMap);
                                player.sendMessage(ChatColor.GRAY + "GT: "
                                        + ChatColor.AQUA + PVPGT);
                                player.sendMessage(ChatColor.GRAY + "Score: "
                                        + ChatColor.AQUA + score);
                                return true;
                            }
                            return true;
                        }
                        player.sendMessage(MCMEPVP.negativecolor
                                + "You're not an Admin!");
                        return true;
                    }
                    // SET
                    if (method.equalsIgnoreCase("set")) {
                        if (player.hasPermission("mcmepvp.set")) {
                            if (args.length > 1) {
                                Vector loc = player.getLocation().toVector();
                                if (args[1].equalsIgnoreCase("blue")) {
                                    p.getConfig()
                                            .set(PVPMap.toLowerCase() + ".blue",
                                            loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Team Blue's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("red")) {
                                    p.getConfig().set(
                                            PVPMap.toLowerCase() + ".red", loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Team Red's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("green")) {
                                    p.getConfig().set(
                                            PVPMap.toLowerCase() + ".green",
                                            loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Team Green's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("purple")) {
                                    p.getConfig().set(
                                            PVPMap.toLowerCase() + ".purple",
                                            loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Team Purple's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("spectator")) {
                                    p.getConfig()
                                            .set(PVPMap.toLowerCase()
                                            + ".spectator", loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Spectator's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("fighter")) {
                                    p.getConfig().set(
                                            PVPMap.toLowerCase() + ".fighter",
                                            loc);
                                    p.saveConfig();
                                    player.sendMessage(MCMEPVP.primarycolor
                                            + "Saved your Location as Fighter's Spawn for Map '"
                                            + PVPMap + "'!");
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("flag")) {
                                    int i = Integer.valueOf(args[2]);
                                    if (i >= 0 || i <= 5) {
                                        String[] msg = new String[2];
                                        msg[0] = PVPMap;
                                        msg[1] = String.valueOf(i);
                                        loc.setY(loc.getY() - 2);
                                        p.getConfig().set(
                                                PVPMap.toLowerCase() + ".Flag"
                                                + i, loc);
                                        p.saveConfig();
                                        util.notifyAdmin(player.getName(),
                                                8, msg);
                                        return true;
                                    }
                                }
                                if (args[1].equalsIgnoreCase("region")) {
                                    if (args[2].equalsIgnoreCase("Eriador")
                                            || args[2].equalsIgnoreCase("Rohan")
                                            || args[2].equalsIgnoreCase("Lothlorien")) {
                                        p.getConfig().set(
                                                PVPMap.toLowerCase() + ".region", args[2]);
                                        p.saveConfig();
                                        player.sendMessage(MCMEPVP.primarycolor
                                                + "Saved region " + args[2] + " for "
                                                + PVPMap + "'!");
                                        return true;
                                    }
                                }
                            } else {
                                player.sendMessage("/pvp set [blue|red|green|orange|spectator|fighter]");
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }
                    }
                    // ADD
                    if (method.equalsIgnoreCase("add")) {
                        if (player.hasPermission("mcmepvp.add")) {
                            if (args.length > 1) {
                                if (((List<String>) p.getConfig().getList(
                                        "maps")).contains(args[1])) {
                                    sender.sendMessage("Map already exists!");
                                } else {
                                    ((List<String>) p.getConfig().getList(
                                            "maps")).add(args[1]);
                                    p.saveConfig();
                                    MCMEPVP.Maps.add(args[1]);
                                    util.notifyAdmin(player.getName(), 6,
                                            new String[]{args[1]});
                                    return true;
                                }
                            }
                        }
                    }
                    // REMOVE
                    if (method.equalsIgnoreCase("remove")) {
                        if (player.hasPermission("mcmepvp.remove")) {
                            if (args.length > 1) {
                                if (((List<String>) p.getConfig().getList(
                                        "maps")).contains(args[1])) {
                                    ((List<String>) p.getConfig().getList(
                                            "maps")).remove(args[1]);
                                    p.saveConfig();
                                    MCMEPVP.Maps.remove(args[1]);
                                    util.notifyAdmin(player.getName(), 7,
                                            new String[]{args[1]});
                                    return true;
                                } else {
                                    sender.sendMessage("Map does not exist");
                                }
                            }
                        }
                    }
                    // LIST
                    if (method.equalsIgnoreCase("list")) {
                        if (player.hasPermission("mcmepvp.list")) {
                            if (args.length == 1) {
                                sender.sendMessage(prettyPrint(MCMEPVP.Maps,
                                        "Maps:"));
                                return true;
                            }
                        }
                    }
                    // STOP
                    if (method.equalsIgnoreCase("stop")) {
                        if (player.hasPermission("mcmepvp.stop")) {
                            Bukkit.getServer()
                                    .broadcastMessage(
                                    MCMEPVP.highlightcolor
                                    + "The PVP Event has been aborted by an admin!");
                            resetGame();
                            return true;
                        }
                    }
                    // MAP
                    if (method.equalsIgnoreCase("map")) {
                        if (player.hasPermission("mcmepvp.map")) {
                            if (args.length > 1) {
                                if (GameStatus == 0) {
                                    if (Maps.contains(args[1])) {
                                        PVPMap = args[1];
                                        util.notifyAdmin(player.getName(),
                                                3, null);
                                        return true;
                                    } else {
                                        player.sendMessage(MCMEPVP.negativecolor
                                                + "'"
                                                + args[1]
                                                + "' is not a valid Map!");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(MCMEPVP.negativecolor
                                            + "Can't change Map during a running Game!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(MCMEPVP.primarycolor
                                        + "The current map is "
                                        + MCMEPVP.highlightcolor + PVPMap);
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }
                    }
                    //GT
                    if (method.equalsIgnoreCase("gt")) {
                        if (player.hasPermission("mcmepvp.gt")) {
                            if (args.length > 1) {
                                if (GameStatus == 0) {
                                    if (GameTypes.contains(args[1])) {
                                        PVPGT = args[1];
                                        util.notifyAdmin(player.getName(),
                                                4, null);
                                        return true;
                                    } else {
                                        player.sendMessage(MCMEPVP.negativecolor
                                                + "'"
                                                + args[1]
                                                + "' is not a valid gametype!");
                                        return true;
                                    }
                                } else {
                                    player.sendMessage(MCMEPVP.negativecolor
                                            + "Can't change GameType during a running Game!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(MCMEPVP.primarycolor
                                        + "The current gametype is "
                                        + MCMEPVP.highlightcolor + PVPGT);
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }
                    }
                    //SETSCORE
                    if (method.equalsIgnoreCase("setscore")) {
                        if (player.hasPermission("mcmepvp.setscore")) {
                            if (args[1] != null) {
                                boolean gameon = false;
                                String desiredgt = args[1].toUpperCase();
                                if (args[2] != null) {
                                    int desiredscore = Integer.valueOf(args[2]);
                                    if (GameStatus == 1
                                            && PVPGT.equalsIgnoreCase(desiredgt)) {
                                        
                                        Bukkit.broadcastMessage(MCMEPVP.positivecolor
                                                + "To win, you must now score "
                                                + MCMEPVP.highlightcolor
                                                + desiredscore
                                                + MCMEPVP.positivecolor
                                                + " points.");
                                        gameon = true;
                                    }
                                    switch (desiredgt) {
                                        case "TSL": {
                                            String[] msg = new String[2];
                                            msg[0] = "TSL";
                                            msg[1] = String.valueOf(desiredscore);
                                            config.TSLscore = desiredscore;
                                            p.getConfig().set("score.TSL",
                                                    desiredscore);
                                            p.saveConfig();
                                            util.notifyAdmin(player.getName(),
                                                    5, msg);
                                            if (gameon){
                                                MCMEPVP.CurrentGame.getObjective().setDisplayName("Score: "+config.TSLscore);
                                            }
                                            return true;
                                        }
                                        case "TCQ": {
                                            String[] msg = new String[2];
                                            msg[0] = "TCQ";
                                            msg[1] = String.valueOf(desiredscore);
                                            config.TCQscore = desiredscore;
                                            p.getConfig().set("score.TCQ",
                                                    desiredscore);
                                            p.saveConfig();
                                            util.notifyAdmin(player.getName(),
                                                    5, msg);
                                            return true;
                                        }
                                        default: {
                                            player.sendMessage(MCMEPVP.negativecolor
                                                    + "/pvp setscore [gt] [score]");
                                            return true;
                                        }
                                    }
                                } else {
                                    player.sendMessage(MCMEPVP.negativecolor
                                            + "/pvp setscore [gt] [score]");
                                    return true;
                                }

                            } else {
                                player.sendMessage(MCMEPVP.negativecolor
                                        + "/pvp setscore [gt] [score]");
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }

                    }
                    if (method.equalsIgnoreCase("unlock")) {
                        if (player.hasPermission("mcmepvp.unlock")) {
                            locked = false;
                            util.notifyAdmin(player.getName(), 9, null);
                            return true;
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }

                    }
                    if (method.equalsIgnoreCase("lock")) {
                        if (player.hasPermission("mcmepvp.lock")) {
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!p.hasPermission("mcmepvp.ignorelock")) {
                                    p.kickPlayer("PVP is over! Come Back Soon!");
                                }
                            }
                            locked = true;
                            util.notifyAdmin(player.getName(), 10, null);
                            return true;
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }
                    }
                    if (method.equalsIgnoreCase("forcejoin")) {
                        if (player.hasPermission("mcmepvp.forcejoin")) {
                            if (args[1] != null) {
                                if (GameStatus == 1) {
                                    CurrentGame.addTeam(player,
                                            args[1].toLowerCase());
                                    util.notifyAdmin(player.getName(), 11,
                                            new String[]{args[1]});
                                    return true;
                                } else {
                                    player.sendMessage(MCMEPVP.negativecolor
                                            + "No game running, no team to join.");
                                }
                            } else {
                                player.sendMessage(MCMEPVP.negativecolor
                                        + "You need to provide a team to join!");
                                return true;
                            }
                        } else {
                            player.sendMessage(MCMEPVP.negativecolor
                                    + "You're not an Admin!");
                            return true;
                        }

                    }

                }
                return true;
            }
            // SHOUT
            if (label.equalsIgnoreCase("shout")) {
                if (player.hasPermission("mcmepvp.shout")) {
                    if (args.length != 0) {
                        String msg = args[0];
                        if (args.length > 1) {
                            for (int i = 1; i < args.length; i++) {
                                msg += " " + args[i];
                            }
                        }
                        if (player.hasPermission("mcmepvp.adminshout")) {
                            Bukkit.getServer().broadcastMessage(
                                    MCMEPVP.admincolor + "Admin "
                                    + player.getName()
                                    + MCMEPVP.shoutcolor + " shouts: "
                                    + MCMEPVP.primarycolor + msg);
                            return true;
                        } else {
                            if (getPlayerTeam(player).equals("spectator")) {
                                player.sendMessage(MCMEPVP.negativecolor
                                        + "Spectators aren't allowed to shout!");
                                return true;
                            } else {
                                Bukkit.getServer().broadcastMessage(
                                        MCMEPVP.shoutcolor + player.getName()
                                        + " shouts: " + msg);
                                return true;
                            }
                        }
                    }
                } else {
                    player.sendMessage(MCMEPVP.negativecolor
                            + "You are not allowed to shout!");
                    return false;
                }
            }
            // ADMINCHAT
            if (label.equalsIgnoreCase("a")) {
                if (player.hasPermission("mcmepvp.adminchat")) {
                    if (args.length != 0) {
                        String msg = args[0];
                        if (args.length > 1) {
                            for (int i = 1; i < args.length; i++) {
                                msg += " " + args[i];
                            }
                        }
                        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
                            if (currentplayer
                                    .hasPermission("mcmepvp.adminchat")) {
                                currentplayer.sendMessage(ChatColor.WHITE + "[" + MCMEPVP.admincolor
                                        + "A" + ChatColor.WHITE + "] " + MCMEPVP.admincolor + player.getName() + ": " + msg);
                            }
                        }
                    }
                    return true;
                } else {
                    player.sendMessage(MCMEPVP.negativecolor
                            + "You are not an Admin!");
                    return false;
                }
            }
        }
        return true;
    }

    private String prettyPrint(List<String> list, String title) {
        StringBuilder out = new StringBuilder();
        out.append(MCMEPVP.primarycolor).append(title).append("\n");
        int count = list.size();
        int i = 0;
        for (String item : list) {
            out.append(MCMEPVP.primarycolor).append("- ")
                    .append(MCMEPVP.highlightcolor).append(item);
            if (i < count) {
                out.append("\n");
            }
            i++;
        }
        return out.toString();
    }

    public int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }

    public int setScore(int size) {
        int i = 10;
        if (size <= 10) {
            i = 25;
        }
        if (size > 10 && size <= 15) {
            i = 30;
        }
        if (size > 15 && size <= 20) {
            i = 40;
            return i;
        }
        if (size > 20 && size <= 30) {
            i = 50;
            return i;
        }
        if (size > 30) {
            i = 75;
        }
        return i;
    }
}
