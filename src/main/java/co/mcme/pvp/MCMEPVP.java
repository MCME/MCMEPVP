package co.mcme.pvp;

import static co.mcme.pvp.gametypes.ringBearerGame.ringBearers;
import static co.mcme.pvp.listeners.flagListener.BlockFlagMarkers;
import static co.mcme.pvp.listeners.flagListener.CarpetFlagMarkers;
import static co.mcme.pvp.listeners.flagListener.Flags;
import static co.mcme.pvp.listeners.flagListener.blueFlagCount;
import static co.mcme.pvp.listeners.flagListener.redFlagCount;
import static co.mcme.pvp.util.config.LogDelay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import co.mcme.pvp.commands.mapCommands;
import co.mcme.pvp.commands.pvpCommands;
import co.mcme.pvp.commands.methods.voteCmdMethods;
import co.mcme.pvp.gametypes.freeForAllGame;
import co.mcme.pvp.gametypes.infectionGame;
import co.mcme.pvp.gametypes.ringBearerGame;
import co.mcme.pvp.gametypes.teamConquestGame;
import co.mcme.pvp.gametypes.teamDeathMatchGame;
import co.mcme.pvp.gametypes.teamSlayerGame;
import co.mcme.pvp.listeners.blockListener;
import co.mcme.pvp.listeners.chatListener;
import co.mcme.pvp.listeners.damageListener;
import co.mcme.pvp.listeners.flagListener;
import co.mcme.pvp.listeners.horseListener;
import co.mcme.pvp.listeners.magicItemListener;
import co.mcme.pvp.listeners.pingListener;
import co.mcme.pvp.listeners.playerListener;
import co.mcme.pvp.listeners.signListener;
import co.mcme.pvp.listeners.statsListener;
import co.mcme.pvp.listeners.weatherListener;
import co.mcme.pvp.lobby.lobbyMode;
import co.mcme.pvp.lobby.lobbyType;
import co.mcme.pvp.maps.Spawn;
import co.mcme.pvp.maps.pvpMap;
import co.mcme.pvp.maps.pvpMapMeta;
import co.mcme.pvp.stats.DataManager;
import co.mcme.pvp.stats.entry.GameEntry;
import co.mcme.pvp.stats.entry.JoinEntry;
import co.mcme.pvp.stats.entry.KillEntry;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.gearGiver;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.teamUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;
import co.mcme.pvp.util.worldUtils;

public class MCMEPVP extends JavaPlugin {

    public static gameType CurrentGame;
    public static lobbyType CurrentLobby;
    public static pvpMap CurrentMap;
    
    public static HashMap<String, String> PlayerStatus;
    public static HashSet<String> adminChat = new HashSet<String>();
    public static int GameStatus;
    public static int Participants;
    public static World PVPWorld;
    public static String lastMap = "null";
    public static String lastGT = "null";
    public static Location Spawn;
    public static String PVPGT;
    public static String pref = "";
    public static String suff = "";
    public static HashMap<Integer, Vector> extraSpawns;
    public static List<String> Maps;
    public static List<String> GameTypes;
    private static Plugin instance;
    public static boolean canJoin = true;
    public static boolean voteMap = false;
    public static boolean locked = true;
    public static boolean gameDebug = false;
    public static boolean horseMode = false;
    public static boolean autorun =false;
    public static int minOnlinePlayers = 0;
    public static float startThreshHold = 0;
    public static List<String> loot;
    public Configuration conf;
    public config config;
    public static LinkedBlockingQueue<Player> queue;
    public static ChatColor primarycolor = ChatColor.GRAY;
    public static ChatColor highlightcolor = ChatColor.AQUA;
    public static ChatColor negativecolor = ChatColor.RED;
    public static ChatColor positivecolor = ChatColor.GREEN;
    public static ChatColor admincolor = ChatColor.GOLD;
    public static ChatColor shoutcolor = ChatColor.DARK_GRAY;
    public static Plugin voxel = Bukkit.getServer().getPluginManager().getPlugin("VoxelSniper");

    public MCMEPVP() {
        super();
        instance = this;
    }

    public static Plugin inst() {
        return instance;
    }

    @Override
    public void onDisable() {
        DataManager.close();
        if(GameStatus == 1){
        	CurrentGame.clearBoard();
        	repairExplosions();
        }
    }

    @Override
    public void onEnable() {
        queue = new LinkedBlockingQueue<Player>();
        //loadLoot();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        conf = this.getConfig();
        config = new config();
        PluginManager pm = getServer().getPluginManager();
        //registering Listeners
        registerEvents();
        PlayerStatus = new HashMap<String, String>();
        
        config.setPVPDefaults();
        
        if (autorun) {
        	System.out.print("[MCMEPVP] (Lobby) Auto-lobby: " + autorun);
        	System.out.print("[MCMEPVP] (Lobby) Min online players: " + minOnlinePlayers);
        	System.out.print("[MCMEPVP] (Lobby) StartThreshHold: " + startThreshHold);
        	System.out.print("[MCMEPVP] (Lobby) Map Voting: " + voteMap);
        }
        
        resetGame();
        getCommand("pvp").setExecutor(new pvpCommands(this));
        getCommand("shout").setExecutor(new pvpCommands(this));
        getCommand("a").setExecutor(new pvpCommands(this));
        getCommand("vote").setExecutor(new pvpCommands(this));
        getCommand("map").setExecutor(new mapCommands(this));
        
        getServer().getScheduler().runTask(this, new Runnable() {
			@Override
			public void run() {
				autoUnlock();
				
			}
        });
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
        	public void run() {
                util.debug("Players reminded of stats!");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(primarycolor + "Don't forget to check your stats at " + highlightcolor + "mcme.co/pvp/stats/" + p.getName());
                }
            }	
        }, co.mcme.pvp.util.config.announceDelay * 20L, co.mcme.pvp.util.config.announceDelay * 20L); 
        try {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new DataManager(this), LogDelay * 20, LogDelay * 20);
        } catch (Exception e) {
            util.severe("Error initiating MCMEPVP database connection, disabling plugin");
            pm.disablePlugin(this);
        }
    }

    public static void resetGame() {
    	canJoin = false;
        if (GameStatus == 1) {
            if (PVPGT.equals("INF")) {
                infectionGame.stopTimer();
                extraSpawns.clear();
            }
            if(PVPGT.equals("FFA")) {
            	freeForAllGame.stopTimer();
                extraSpawns.clear();
            }
            if(PVPGT.equals("RBR")){
            	ringBearerGame.stopTimer();
            }
            repairExplosions();
            CurrentGame.clearBoard();
        }
        
        List<EntityType> removing = new ArrayList<EntityType>();
        removing.add(EntityType.ARROW);
        removing.add(EntityType.DROPPED_ITEM);
        removing.add(EntityType.HORSE);
        int removed = worldUtils.removeEntities(removing);
        System.out.print("Removed: " + removed + " entities from the world.");
        util.debug("Removed " + removed + " entities from the world.");
        
        Participants = 0;
        GameStatus = 0;
        
        horseMode = false;
        setWeather();
        
        PlayerStatus = new HashMap<String, String>();
        
        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
        	currentplayer.setPlayerListName(currentplayer.getName());
        	if(currentplayer.isInsideVehicle()){
            	currentplayer.getVehicle().remove();
            }
            teamUtil.setPlayerTeam(currentplayer, "spectator");
            currentplayer.getInventory().clear();
            currentplayer.setHealth(20);
            currentplayer.setFoodLevel(20);
            statsListener.stripStats(currentplayer);
            textureSwitcher.switchTP(currentplayer);
            spectatorUtil.showAll(currentplayer);
            if(currentplayer.getActivePotionEffects() != null){
            	for(PotionEffect pe : currentplayer.getActivePotionEffects()){
            		currentplayer.removePotionEffect(pe.getType());
            	}
            }
            if(currentplayer.isInsideVehicle()){
            	currentplayer.getVehicle().eject();
            }
        }
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
        		Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {
        			public void run() {
        				for (Player currentplayer : Bukkit.getOnlinePlayers()) {
        					currentplayer.teleport(Spawn);
                        }
        			}
        	}, 60L);
        
        if (!statsListener.playerStats.isEmpty()) {
        	statsListener.playerStats.clear();
        }
        if (PVPGT.equals("TCQ")) {
            Flags.clear();
            blueFlagCount = 0;
            redFlagCount = 0;
            if (BlockFlagMarkers != null) {
                for (List<Block> listBlocks : BlockFlagMarkers.values()) {
                    for (Block bx : listBlocks) {
                        bx.setType(Material.AIR);
                    }
                }
                BlockFlagMarkers.clear();
            }
            if (CarpetFlagMarkers != null) {
                for (List<Block> listBlocks : CarpetFlagMarkers.values()) {
                    for (Block bx : listBlocks) {
                        bx.setType(Material.AIR);
                    }
                }
                CarpetFlagMarkers.clear();
            }
        }
        if (PVPGT.equals("RBR")) {
            ringBearers.clear();
        }
        Bukkit.getServer().getPluginManager().enablePlugin(voxel);
        
        if (CurrentLobby != null) {
        	CurrentLobby.stopLobby();
        	System.out.print("[MCMEPVP] (Lobby) Clearing lobby!");
        }
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(
        		Bukkit.getPluginManager().getPlugin("MCMEPVP"), new Runnable() {

			@Override
			public void run() {
				CurrentLobby = new lobbyMode();
			}
        	
        }, 40L);
    }

    public static void logKill(PlayerDeathEvent event) {
    	if (!PVPGT.equals("INF") && !gameDebug) {
        Player victim = event.getEntity();
        Player killer;
        String victimname = victim.getName();
        String killername = null;
        KillEntry entry = new KillEntry();
            if (victim.getKiller() instanceof Player) {
                killer = victim.getKiller();
                killername = killer.getName();
                util.debug("Killer: " + killername + " Victim: " + victimname);
                util.debug("Kill Sent to logger!");
            } else if (!(victim.getKiller() instanceof Player)) {
                killername = "//ENVIRONMENT//";
                util.debug("Kill Sent to logger!");
            }
            entry.setInfo(victimname, killername, CurrentMap.getName(), PVPGT);
            DataManager.addKillEntry(entry);
        }
    }

    public static void logJoin(String player, String mapname, String gametype, boolean win) {
    	if(!gameDebug){
    		DataManager.addJoinEntry(new JoinEntry(player, mapname, gametype, win));
    	}
    }

    public static void logGame(String winner, String mapname, String gametype) {
    	if(!gameDebug){
    		DataManager.addGameEntry(new GameEntry(winner, mapname, gametype));
    	}
    }

    public static void startGame() {    	
    	if (CurrentLobby != null) {
    		CurrentLobby.clearBoard();
        	CurrentLobby.stopLobby();
    	}
    	
    	if (!voteCmdMethods.hasVoted.isEmpty()) {
    		voteCmdMethods.hasVoted.clear();
    		System.out.print("[MCMEPVP] HasVotedList cleared!");
    	}
    	
    	canJoin = false;
        extraSpawns = new HashMap<Integer, Vector>();
        
        if (PVPGT.equals("TDM")) {
            CurrentGame = new teamDeathMatchGame();
        }
        if (PVPGT.equals("TSL")) {
            CurrentGame = new teamSlayerGame();
        }
        if (PVPGT.equals("TCQ")) {
            CurrentGame = new teamConquestGame();
        }
        if (PVPGT.equals("RBR")) {
            CurrentGame = new ringBearerGame();
        }
        if (PVPGT.equals("INF")) {
            CurrentGame = new infectionGame();
            extraSpawns();
        }
        if (PVPGT.equals("FFA")) {
            CurrentGame = new freeForAllGame();
            extraSpawns();
        }
        if(CurrentGame.allowExplosionLogging()){
        	exploadables();
        }
        if (gameDebug) {
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA + "This is a debug game. Stats will not be recorded!");
        }
        Bukkit.getServer().getPluginManager().disablePlugin(voxel);
    }

	private static void extraSpawns() {
		pvpMapMeta meta = CurrentMap.getMapMeta();
		int i = 0;
		for (Spawn s : meta.getSpawns().values()) {
			extraSpawns.put(i, s.toVector());
			i++;
		}
		for (Vector v : meta.getFlags().values()) {
			extraSpawns.put(i, v);
			i++;
		}
    }
    
    private static void autoUnlock(){
    	for (Player p : Bukkit.getOnlinePlayers()) {
    		if (!p.hasPermission("mcmepvp.admin")) {
    			locked = false;
    			break;
    		}
    	}
    }
    
    public static void setWeather(){
    	if (GameStatus == 1) {
    		int i = gearGiver.getRandom(0, 2);
    		if (i == 2) {
    			if (CurrentMap.getName().equalsIgnoreCase("HelmsDeep")) {
            		PVPWorld.setTime(15000);
            		PVPWorld.setStorm(true);
            		PVPWorld.setThundering(true);
            	}
    		}
    	} else {
    		PVPWorld.setStorm(false);
    		PVPWorld.setThundering(false);
    	}
    }
    
    private static void exploadables(){
    	blockListener.explodeableList.add(44);
    	blockListener.explodeableList.add(45);
    	blockListener.explodeableList.add(108);
    	blockListener.explodeableList.add(113);
    	//TODO load custom exploadable blocks from config on a per-map basis!
    }
    
    private static void repairExplosions(){
    	if(CurrentGame.allowExplosionLogging() && !blockListener.explodedBlocks.isEmpty()){
    		for(Location l : blockListener.explodedBlocks.keySet()){
    			List<Integer> block = blockListener.explodedBlocks.get(l);
    			int type = block.get(0);
    			byte data = block.get(1).byteValue();
    			l.getBlock().setTypeId(type);
    			l.getBlock().setData(data);
    		}
    		blockListener.explodedBlocks.clear();
    		blockListener.explodeableList.clear();
    	}
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new chatListener(this), this);
        getServer().getPluginManager().registerEvents(new damageListener(this), this);
        getServer().getPluginManager().registerEvents(new playerListener(this), this);
        getServer().getPluginManager().registerEvents(new pingListener(), this);
        getServer().getPluginManager().registerEvents(new signListener(), this);
        getServer().getPluginManager().registerEvents(new flagListener(), this);
        getServer().getPluginManager().registerEvents(new magicItemListener(), this);
        getServer().getPluginManager().registerEvents(new blockListener(), this);
        getServer().getPluginManager().registerEvents(new horseListener(), this);
        getServer().getPluginManager().registerEvents(new statsListener(), this);
        getServer().getPluginManager().registerEvents(new weatherListener(), this);
    }

    public static void queuePlayer(Player player) {
        if (!teamUtil.isOnTeam(player)) {
        	statsListener.stripStats(player);
            if (GameStatus == 1) {
                CurrentGame.addPlayerDuringGame(player);
                return;
            }
            if (teamUtil.getPlayerTeam(player).equals("participant")) {
                player.sendMessage(negativecolor + "You are already participating in the next Game!");
            } else {
                queue.add(player);
                Participants++;
                if (GameStatus == 0) {
                	CurrentLobby.setTeam(player, "participant");
                }
                teamUtil.setPlayerTeam(player, "participant");
                player.sendMessage(positivecolor + "You are participating! Wait for the next Game to start!");
                if(MCMEPVP.GameStatus==0){
                	player.teleport(MCMEPVP.Spawn);
                }
                util.notifyAdmin(player.getName(), 1, null);
            }
        }
    }

    public static void unQueuePlayer(Player player) {
        if (queue.contains(player)) {
            queue.remove(player);
            Participants--;
            if (GameStatus == 0) {
            	CurrentLobby.setTeam(player, "participant");
            }
            teamUtil.setPlayerTeam(player, "spectator");
            player.sendMessage(negativecolor + "You are no longer participating!");
            util.notifyAdmin(player.getName(), 2, null);
        } else {
            util.debug("Player `" + player.getName() + "` was not queued, but attempted to unqueue!");
        }
    }
    
    public static boolean isQueued(Player player){
        return queue.contains(player);
    }

    public static void determineSpawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Spawn);
    }
    
    public static void winFireworks(String team) {
		Builder builder = FireworkEffect.builder();
		
		FireworkEffect ffx = null;
		if (team.equals("red")) {
			builder.withColor(Color.RED);
			builder.withTrail();
			builder.withFlicker();
			builder.with(FireworkEffect.Type.CREEPER);
		}
		if (team.equals("blue")) {
			builder.withColor(Color.BLUE);
			builder.withTrail();
			builder.withFlicker();
			builder.with(FireworkEffect.Type.STAR);
		}
		ffx = builder.build();
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location l = p.getLocation();
			World w = p.getWorld();
			
			Firework fw = w.spawn(l, Firework.class);
			FireworkMeta fwm = fw.getFireworkMeta();
			fwm.clearEffects();
			fwm.addEffects(ffx);
			
			Field f;
			try {
				f = fwm.getClass().getDeclaredField("power");
				f.setAccessible(true);
				f.set(fwm, -2);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fw.setFireworkMeta(fwm);
		}
	}
}
