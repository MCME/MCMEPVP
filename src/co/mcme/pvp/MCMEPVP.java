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
import co.mcme.pvp.listeners.chatListener;
import co.mcme.pvp.listeners.damageListener;
import co.mcme.pvp.listeners.inventoryListener;
import co.mcme.pvp.listeners.playerListener;
import co.mcme.pvp.util.SpectatorTools;

public class MCMEPVP extends JavaPlugin {

    public static Game CurrentGame;
    public static HashMap<String, String> PlayerStatus;
    public static int GameStatus;
    public static int Participants;
    public static World PVPWorld;
    public static String PVPMap;
    public static String GT;
    public static HashMap<String, Vector> Spawns;

    @Override
    public void onEnable() {
        //registering Listener
        registerEvents();
        PVPMap = (String) this.getConfig().get("general.defaultMap");
        GT = (String) this.getConfig().get("general.defaultGameType");
        PVPWorld = Bukkit.getWorld((String) this.getConfig().get("general.defaultWorld"));
        resetGame();
        Spawns.put("blue", (Vector) this.getConfig().get("spawns.spectator"));
        Spawns.put("red", (Vector) this.getConfig().get("spawns.red"));
        Spawns.put("spectator", (Vector) this.getConfig().get("spawns.spectator"));
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
                            player.sendMessage(ChatColor.YELLOW + "You are participating! Wait for the next Game to start!");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED
                                + "You can't join a running Game!");
                        return true;
                    }
                }
                //START
                if (method.equalsIgnoreCase("start")) {
                    if (player.hasPermission("mcmepvp.admin")) {
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
                    if (player.hasPermission("mcmepvp.admin")) {
                        if (args.length > 1) {
                            Vector loc = player.getLocation().toVector();
                            if (args[1].equalsIgnoreCase("blue")) {
                                Spawns.put("blue", loc);
                                this.getConfig().set("spawns.blue", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW + "Saved your Location as Team Blue's Spawn!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("red")) {
                                Spawns.put("red", loc);
                                this.getConfig().set("spawns.red", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW + "Saved your Location as Team Red's Spawn!");
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("spectator")) {
                                Spawns.put("spectator", loc);
                                this.getConfig().set("spawns.spectator", loc);
                                this.saveConfig();
                                player.sendMessage(ChatColor.YELLOW + "Saved your Location as Spectator's Spawn!");
                                return true;
                            }
                        } else {
                            player.sendMessage("/pvp set [blue|red|spectator|class]");
                            return true;
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You're not an Admin!");
                        return true;
                    }
                }
                //STOP
                if (method.equalsIgnoreCase("stop")) {
                    if (player.hasPermission("mcmepvp.admin")) {
                        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW
                                + "The PVP Event has been aborted by an admin!");
                        resetGame();
                        return true;
                    }
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("shout")) {
        }
        return false;
    }

    public static void resetGame() {
        Participants = 0;
        GameStatus = 0;
        PlayerStatus = new HashMap<String, String>();
        Spawns = new HashMap<String, Vector>();
        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
            setPlayerStatus(currentplayer, "spectator", ChatColor.WHITE);
        }
    }

    void startGame() {
        Spawns.put("blue", (Vector) this.getConfig().get("spawns.spectator"));
        Spawns.put("red", (Vector) this.getConfig().get("spawns.red"));
        Spawns.put("spectator", (Vector) this.getConfig().get("spawns.spectator"));
        CurrentGame = new TDMGame();
    }

    public static void setPlayerStatus(Player player, String status, ChatColor NameColor) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        PlayerStatus.put(player.getName(), status);
        player.setPlayerListName(NameColor + player.getName());
        player.setDisplayName(NameColor + player.getName());
        if (status.equalsIgnoreCase("spectator")) {
            SpectatorTools.hide(player);
        }
        if (!(status.equalsIgnoreCase("spectator"))) {
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
