package co.mcme.pvp.util;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.util.Vector;

public class config {

    private static Configuration conf;

    public config() {
        loadConfiguration();
    }
    //Game variables
    public static boolean debug;
    public static boolean autorun;
    public static List<String> Maps;
    public static List<String> GameTypes;
    public static String PVPMap;
    public static String PVPGT;
    public static World PVPWorld;
    public static Vector SpawnVec;
    public static Location Spawn;
    public static long announceDelay;
    public static double startThreshHold;
    public static int minOnlinePlayers;
    public static int TSLscore;
    public static int TCQscore;
    public static int FFATimeLimit;
    public static int INFTimeLimit;
    public static int ZombieHealth;
    //SQL variables
    public static String DbPlayerTable;
    public static String DbKillTable;
    public static String DbWinTable;
    public static String DbGameTable;
    public static String DbUser;
    public static String DbPass;
    public static String DbUrl;
    public static int LogDelay;

    public static void loadConfiguration() {
        conf = Bukkit.getPluginManager().getPlugin("MCMEPVP").getConfig();
        debug = conf.getBoolean("general.debug");
        DbPlayerTable = conf.getString("sql.playertable");
        DbKillTable = conf.getString("sql.killtable");
        DbWinTable = conf.getString("sql.wintable");
        DbGameTable = conf.getString("sql.gametable");
        DbUser = conf.getString("sql.user");
        DbPass = conf.getString("sql.password");
        DbUrl = "jdbc:mysql://" + conf.getString("sql.host") + ":" + conf.getString("sql.port") + "/" + conf.getString("sql.database");
        LogDelay = conf.getInt("sql.logdelay");
        Maps = conf.getStringList("maps");
        GameTypes = conf.getStringList("gametypes");
        PVPMap = conf.getString("general.defaultMap");
        PVPGT = conf.getString("general.defaultGameType");
        PVPWorld = Bukkit.getWorld(conf.getString("general.defaultWorld"));
        SpawnVec = conf.getVector("general.spawn");
        Spawn = new Location(PVPWorld, SpawnVec.getX(), SpawnVec.getY() + 0.5, SpawnVec.getZ());
        announceDelay = conf.getLong("general.announcedelay");
        TSLscore = conf.getInt("score.TSL");
        TCQscore = conf.getInt("score.TCQ");
        INFTimeLimit = conf.getInt("infection.time");
        ZombieHealth = conf.getInt("infection.zombiehealth");
        FFATimeLimit = conf.getInt("freeforall.time");
        autorun = conf.getBoolean("lobby.enableAutoLobby");
        minOnlinePlayers = conf.getInt("lobby.minOnlinePlayers");
        startThreshHold = conf.getDouble("lobby.startThreshHold");
    }
}
