package co.mcme.pvp;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import co.mcme.pvp.gametypes.TDMGame;
import co.mcme.pvp.gametypes.LMSGame;
import co.mcme.pvp.listeners.chatListener;
import co.mcme.pvp.listeners.damageListener;
import co.mcme.pvp.listeners.inventoryListener;
import co.mcme.pvp.listeners.playerListener;
import co.mcme.pvp.util.SpectatorTools;
import java.util.List;
import org.bukkit.Location;

public class MCMEPVP extends JavaPlugin {

    public static Game CurrentGame;
    public static HashMap<String, String> PlayerStatus;
    public static int GameStatus;
    public static int Participants;
    public static World PVPWorld;
    public static String PVPMap;
    public static Location Spawn;
    public static String PVPGT;
    public static HashMap<String, Vector> Spawns;
    public static List<String> Maps;
    public static List<String> GameTypes;

    @Override
    public void onEnable() {
        //registering Listener
        registerEvents();
        PlayerStatus = new HashMap<String, String>();
        Maps = this.getConfig().getStringList("maps");
        GameTypes = this.getConfig().getStringList("gametypes");
        PVPMap = (String) this.getConfig().get("general.defaultMap");
        PVPGT = (String) this.getConfig().get("general.defaultGameType");
        PVPWorld = Bukkit.getWorld((String) this.getConfig().get("general.defaultWorld"));
        Vector SpawnVec = (Vector) this.getConfig().get("general.spawn");
        Spawn = new Location(PVPWorld, SpawnVec.getX(), SpawnVec.getY(), SpawnVec.getZ());
        resetGame();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        //Identify the player
        final Player player = sender.getServer().getPlayer(sender.getName());
        if (cmd.getName().equalsIgnoreCase("pvp")) {
            //What to do when a player types /pvp
            if (args.length != 0) {
                String method = args[0];
                //JOIN
                if (method.equalsIgnoreCase("join")) {
                    if (player.hasPermission("mcmepvp.join")) {
                        if (GameStatus == 0) {
                            //Check if player is participating already
                            if (!(getPlayerStatus(player).equals("spectator"))) {
                                player.sendMessage(ChatColor.DARK_RED
                                        + "You are already participating in the next Game!");
                                return true;
                            } else {
                                //queuePlayer
                                Participants++;
                                setPlayerStatus(player, "participant", ChatColor.GREEN);
                                player.sendMessage(ChatColor.YELLOW
                                        + "You are participating! Wait for the next Game to start!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "You can't join a running Game!");
                            return true;
                        }
                    }
                }
                //START
                if (method.equalsIgnoreCase("start")) {
                    if (player.hasPermission("mcmepvp.start")) {
                        if (GameStatus == 0) {
                            if (Participants >= 2) {
                                startGame();
                                return true;
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + "There need to be at least two participants!");
                                return true;
                            }
                        } else {
                            player.sendMessage(ChatColor.DARK_RED
                                    + "Game already running!");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You're not an Admin!");
                        return true;
                    }
                }
                //SET
                if (method.equalsIgnoreCase("set")) {
                    if (player.hasPermission("mcmepvp.set")) {
                        if (args.length > 1) {
                            Vector loc = player.getLocation().toVector();
                            if (args[1].equalsIgnoreCase("blue")) {
                                this.getConfig().set(PVPMap.toLowerCase() + ".blue", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Team Blue's Spawn for Map '" + PVPMap + "'!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("red")) {
                                this.getConfig().set(PVPMap.toLowerCase() + ".red", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Team Red's Spawn for Map '" + PVPMap + "'!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("spectator")) {
                                this.getConfig().set(PVPMap.toLowerCase() + ".spectator", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Spectator's Spawn for Map '" + PVPMap + "'!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("fighter")) {
                                this.getConfig().set(PVPMap.toLowerCase() + ".fighter", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW
                                        + "Saved your Location as Fighter's Spawn for Map '" + PVPMap + "'!");
                                return true;
                            }
                        } else {
                            player.sendMessage("/pvp set [blue|red|spectator|fighter]");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You're not an Admin!");
                        return true;
                    }
                }
                //STOP
                if (method.equalsIgnoreCase("stop")) {
                    if (player.hasPermission("mcmepvp.stop")) {
                        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW
                                + "The PVP Event has been aborted by an admin!");
                        resetGame();
                        return true;
                    }
                }
                //MAP
                if (method.equalsIgnoreCase("map")) {
                    if (player.hasPermission("mcmepvp.map")) {
                        if (args.length > 1) {
                            if (GameStatus == 0) {
                                if (Maps.contains(args[1])) {
                                    PVPMap = args[1];
                                    player.sendMessage(ChatColor.YELLOW
                                            + "Changed current Map to '" + PVPMap + "'!");
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + "'" + args[1] + "' is not a valid Map!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + "Can't change Map during a running Game!");
                                return true;
                            }
                        } else {
                            player.sendMessage("Current Map: '" + PVPMap + "'");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You're not an Admin!");
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
                                    player.sendMessage(ChatColor.YELLOW
                                            + "Changed current GameType to '" + PVPGT + "'!");
                                    return true;
                                } else {
                                    player.sendMessage(ChatColor.DARK_RED
                                            + "'" + args[1] + "' is not a valid Map!");
                                    return true;
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED
                                        + "Can't change GameType during a running Game!");
                                return true;
                            }
                        } else {
                            player.sendMessage("Current GameType: '" + PVPGT + "'");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You're not an Admin!");
                        return true;
                    }
                }
            }
        }
        //SHOUT
        if (cmd.getName().equalsIgnoreCase("shout")) {
            if (player.hasPermission("mcmepvp.shout")) {
                if (args.length != 0) {
                    String msg = args[0];
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            msg += " " + args[i];
                        }
                    }
                    if (player.hasPermission("mcmepvp.adminshout")) {
                        Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Admin " + player.getName() + ChatColor.GRAY + " shouts: " + ChatColor.WHITE + msg);
                        return true;
                    } else {
                        if (getPlayerStatus(player).equals("spectator")) {
                            player.sendMessage(ChatColor.DARK_RED + "Spectators aren't allowed to shout!");
                            return true;
                        } else {
                            Bukkit.getServer().broadcastMessage(ChatColor.GRAY + player.getName() + " shouts: " + msg);
                            return true;
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You are not allowed to shout!");
                return true;
            }
        }
        //ADMINCHAT
        if (cmd.getName().equalsIgnoreCase("a")) {
            if (player.hasPermission("mcmepvp.adminchat")) {
                if (args.length != 0) {
                    String msg = args[0];
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            msg += " " + args[i];
                        }
                    }
                    for (Player currentplayer : Bukkit.getOnlinePlayers()) {
                        if (currentplayer.hasPermission("mcmepvp.adminchat")) {
                            currentplayer.sendMessage(ChatColor.GOLD + player.getName() + ": " + msg);
                        }
                    }
                }
                return true;
            } else {
                player.sendMessage(ChatColor.DARK_RED + "You are not an Admin!");
                return true;
            }
        }
        return false;
    }

    public static void resetGame() {
        Participants = 0;
        GameStatus = 0;
        PlayerStatus = new HashMap<String, String>();
        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
            setPlayerStatus(currentplayer, "spectator", ChatColor.WHITE);
            currentplayer.teleport(Spawn);
        }
    }

    void startGame() {
        Spawns = new HashMap<String, Vector>();
        Spawns.put("blue", (Vector) this.getConfig().get(PVPMap + ".blue"));
        Spawns.put("red", (Vector) this.getConfig().get(PVPMap + ".red"));
        Spawns.put("spectator", (Vector) this.getConfig().get(PVPMap + ".spectator"));
        if(PVPGT == "TDM"){
            CurrentGame = new TDMGame();
        }
        if(PVPGT == "LMS"){
            CurrentGame = new LMSGame();
        }
    }

    public static void setPlayerStatus(Player player, String status, ChatColor NameColor) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        PlayerStatus.put(player.getName(), status);
        player.setPlayerListName(NameColor + player.getName());
        player.setDisplayName(NameColor + player.getName());
        if (status.equalsIgnoreCase("spectator")) {
            SpectatorTools.hide(player);
        } else {
            SpectatorTools.show(player);
        }
    }

    public static String getPlayerStatus(Player player) {
        String status = PlayerStatus.get(player.getName());
        return status;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new chatListener(this), this);
        getServer().getPluginManager().registerEvents(new damageListener(this), this);
        getServer().getPluginManager().registerEvents(new inventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new playerListener(this), this);
    }
}
