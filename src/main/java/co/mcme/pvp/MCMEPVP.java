package co.mcme.pvp;

import static co.mcme.pvp.gametypes.ringBearerGame.ringBearers;
import static co.mcme.pvp.listeners.flagListener.BlockFlagMarkers;
import static co.mcme.pvp.listeners.flagListener.CarpetFlagMarkers;
import static co.mcme.pvp.listeners.flagListener.Flags;
import static co.mcme.pvp.listeners.flagListener.blueFlagCount;
import static co.mcme.pvp.listeners.flagListener.redFlagCount;
import static co.mcme.pvp.util.config.LogDelay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.kitteh.tag.TagAPI;

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
import co.mcme.pvp.listeners.tagListener;
import co.mcme.pvp.stats.DataManager;
import co.mcme.pvp.stats.entry.GameEntry;
import co.mcme.pvp.stats.entry.JoinEntry;
import co.mcme.pvp.stats.entry.KillEntry;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.spectatorUtil;
import co.mcme.pvp.util.textureSwitcher;
import co.mcme.pvp.util.util;
import co.mcme.pvp.util.worldUtils;

public class MCMEPVP extends JavaPlugin {

    public static gameType CurrentGame;
    public static HashMap<String, String> PlayerStatus;
    public static int GameStatus;
    public static int Participants;
    public static World PVPWorld;
    public static String PVPMap;
    public static Location Spawn;
    public static String PVPGT;
    public static HashMap<String, Vector> Spawns;
    public static HashMap<Integer, Vector> FlagHash;
    public static HashMap<Integer, Vector> extraSpawns;
    public static List<String> Maps;
    public static List<String> GameTypes;
    private static Plugin instance;
    public static boolean locked = true;
    public static boolean debug = false;
    public static List loot;
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
        Maps = config.Maps;
        GameTypes = config.GameTypes;
        PVPMap = config.PVPMap;
        PVPGT = config.PVPGT;
        PVPWorld = config.PVPWorld;
        Spawn = config.Spawn;
        resetGame();
        getCommand("pvp").setExecutor(new pvpCommands(this));
        getCommand("shout").setExecutor(new pvpCommands(this));
        getCommand("a").setExecutor(new pvpCommands(this));
        
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
        Participants = 0;
        GameStatus = 0;
        PlayerStatus = new HashMap<String, String>();
        for (Player currentplayer : Bukkit.getOnlinePlayers()) {
        	if(currentplayer.isInsideVehicle()){
            	currentplayer.getVehicle().remove();
            }
            setPlayerTeam(currentplayer, "spectator");
            currentplayer.teleport(Spawn);
            currentplayer.getInventory().clear();
            currentplayer.setHealth(20);
            currentplayer.setFoodLevel(20);
            textureSwitcher.switchTP(currentplayer);
            spectatorUtil.showAll(currentplayer);
            if(currentplayer.getActivePotionEffects() != null){
            	for(PotionEffect pe : currentplayer.getActivePotionEffects()){
            		currentplayer.removePotionEffect(pe.getType());
            	}
            }
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
            if (FlagHash != null) {
                FlagHash.clear();
            }
        }
        if (PVPGT.equals("RBR")) {
            ringBearers.clear();
        }
        Bukkit.getServer().getPluginManager().enablePlugin(voxel);
        ArrayList<EntityType> removing = new ArrayList<EntityType>();
        removing.add(EntityType.ARROW);
        removing.add(EntityType.DROPPED_ITEM);
        removing.add(EntityType.HORSE);
        int removed = worldUtils.removeEntities(removing);
        util.debug("Removed " + removed + " entities from the world.");
    }

    public static void logKill(PlayerDeathEvent event) {
    	if (!PVPGT.equals("INF") && !debug) {
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
            entry.setInfo(victimname, killername, PVPMap, PVPGT);
            DataManager.addKillEntry(entry);
        }
    }

    public static void logJoin(String player, String mapname, String gametype, boolean win) {
    	if(!debug){
    		DataManager.addJoinEntry(new JoinEntry(player, mapname, gametype, win));
    	}
    }

    public static void logGame(String winner, String mapname, String gametype) {
    	if(!debug){
    		DataManager.addGameEntry(new GameEntry(winner, mapname, gametype));
    	}
    }

    public static void startGame() {
        Spawns = new HashMap<String, Vector>();
        FlagHash = new HashMap<Integer, Vector>();
        extraSpawns = new HashMap<Integer, Vector>();
        loadSpawns();
        if (PVPGT.equals("TDM")) {
            CurrentGame = new teamDeathMatchGame();
        }
        if (PVPGT.equals("TSL")) {
            CurrentGame = new teamSlayerGame();
        }
        if (PVPGT.equals("TCQ")) {
            CurrentGame = new teamConquestGame();
            loadFlags();
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
        Bukkit.getServer().getPluginManager().disablePlugin(voxel);
    }

    private static void loadSpawns() {
        Spawns.put("blue", instance.getConfig().getVector(PVPMap.toLowerCase() + ".blue"));
        Spawns.put("red", instance.getConfig().getVector(PVPMap.toLowerCase() + ".red"));
        Spawns.put("green", instance.getConfig().getVector(PVPMap.toLowerCase() + ".green"));
        Spawns.put("purple", instance.getConfig().getVector(PVPMap.toLowerCase() + ".purple"));
        Spawns.put("spectator", instance.getConfig().getVector(PVPMap.toLowerCase() + ".spectator"));
    }

    private static void loadFlags() {
        int i = 0;
        while (i >= 0 && i <= 5) {
            if (instance.getConfig().contains(PVPMap.toLowerCase() + ".Flag" + i)) {
                FlagHash.put(i, instance.getConfig().getVector(PVPMap.toLowerCase() + ".Flag" + i));
            }
            i++;
        }
    }

    private static void extraSpawns() {
    	extraSpawns.put(0, instance.getConfig().getVector(PVPMap.toLowerCase() + ".blue"));
    	extraSpawns.put(1, instance.getConfig().getVector(PVPMap.toLowerCase() + ".red"));
        int i = 0;
        int t = 2;
        while (i >= 0 && i <= 5) {
            if (instance.getConfig().contains(PVPMap.toLowerCase() + ".Flag" + i)) {
                Vector vec = instance.getConfig().getVector(PVPMap.toLowerCase() + ".Flag" + i);
                Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                Location loc1 = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY(), vec.getZ());
                World w = loc.getWorld();
                loc.setY(loc.getY() + 2);
                loc1.setY(loc1.getY() + 3);
                Block bz = w.getBlockAt(loc);
                Block bz1 = w.getBlockAt(loc1);

                if (bz.getTypeId() == 0 && bz1.getTypeId() == 0) {
                	extraSpawns.put(t, loc.toVector());
                    t++;
                }
            }
            i++;
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

    public static void setPlayerTeam(Player player, String status) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        PlayerStatus.put(player.getName(), status);
        if (status.equals("spectator") || status.equals("participant")) {
            player.setMetadata("god", new FixedMetadataValue(inst(), true));
        }
        if (!(status.equals("spectator") || status.equals("participant"))) {
            player.setMetadata("god", new FixedMetadataValue(inst(), false));
        }
        if (player.isOnline()) {
            TagAPI.refreshPlayer(player);
        }
        if(status != "spectator"){
        	spectatorUtil.setParticipant(player);
        }
        if(status == "spectator"){
        	spectatorUtil.setSpectator(player);
        }
    }

    public static String getPlayerTeam(Player player) {
        String status = "spectator";
        if (PlayerStatus.containsKey(player.getName())) {
            status = PlayerStatus.get(player.getName());
        }
        return status;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new chatListener(this), this);
        getServer().getPluginManager().registerEvents(new damageListener(this), this);
        getServer().getPluginManager().registerEvents(new playerListener(this), this);
        getServer().getPluginManager().registerEvents(new tagListener(), this);
        getServer().getPluginManager().registerEvents(new pingListener(), this);
        getServer().getPluginManager().registerEvents(new signListener(), this);
        getServer().getPluginManager().registerEvents(new flagListener(), this);
        getServer().getPluginManager().registerEvents(new magicItemListener(), this);
        getServer().getPluginManager().registerEvents(new blockListener(), this);
        getServer().getPluginManager().registerEvents(new horseListener(), this);
    }

    public static void queuePlayer(Player player) {
        if (!isOnTeam(player)) {
            if (GameStatus == 1) {
                CurrentGame.addPlayerDuringGame(player);
                return;
            }
            if (getPlayerTeam(player).equals("participant")) {
                player.sendMessage(negativecolor + "You are already participating in the next Game!");
            } else {
                queue.add(player);
                Participants++;
                setPlayerTeam(player, "participant");
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
            setPlayerTeam(player, "spectator");
            player.sendMessage(negativecolor + "You are no longer participating!");
            TagAPI.refreshPlayer(player);
            util.notifyAdmin(player.getName(), 2, null);
        } else {
            util.debug("Player `" + player.getName() + "` was not queued, but attempted to unqueue!");
        }
    }
    
    public static boolean isQueued(Player player){
        return queue.contains(player);
    }

    public static boolean isOnTeam(Player player) {
        boolean check = true;
        if (PlayerStatus.containsKey(player.getName())) {
            String status = getPlayerTeam(player);
            if (status.equals("spectator")) {
                check = false;
            }
            if (status.equals("participant")) {
                check = false;
            }
        } else {
            check = false;
        }
        return check;
    }

    public static void determineSpawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Spawn);
    }
}
