package co.mcme.pvp.stats;

import co.mcme.pvp.stats.entry.GameEntry;
import co.mcme.pvp.stats.entry.JoinEntry;
import co.mcme.pvp.stats.entry.KillEntry;
import co.mcme.pvp.util.config;
import co.mcme.pvp.util.util;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.plugin.Plugin;

public class DataManager extends TimerTask {

    private static final LinkedBlockingQueue<KillEntry> Killqueue = new LinkedBlockingQueue<KillEntry>();
    private static final LinkedBlockingQueue<JoinEntry> Joinqueue = new LinkedBlockingQueue<JoinEntry>();
    private static final LinkedBlockingQueue<GameEntry> Gamequeue = new LinkedBlockingQueue<GameEntry>();
    private static ConnectionManager connections;
    public static final HashMap<String, Integer> dbPlayers = new HashMap<String, Integer>();
    public static Timer loggingTimer = null;

    public DataManager(Plugin instance) throws Exception {
        connections = new ConnectionManager(config.DbUrl, config.DbUser, config.DbPass);
        getConnection().close();

        //Check tables and update player list
        if (!checkTables()) {
            throw new Exception();
        }
        if (!updateDbLists()) {
            throw new Exception();
        }
        //Start logging timer
        loggingTimer = new Timer();
        loggingTimer.scheduleAtFixedRate(this, 2000, 2000);
    }

    public static void close() {
        connections.close();
        if (loggingTimer != null) {
            loggingTimer.cancel();
        }
    }

    public static void addKillEntry(KillEntry entry) {
        Killqueue.add(entry);
    }

    public static void addJoinEntry(JoinEntry entry) {
        Joinqueue.add(entry);
    }

    public static void addGameEntry(GameEntry entry) {
        Gamequeue.add(entry);
    }

    public static String getPlayer(int id) {
        for (Entry<String, Integer> entry : dbPlayers.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }

        }

        return null;
    }

    public static JDCConnection getConnection() {
        try {
            return connections.getConnection();
        } catch (final SQLException ex) {
            util.severe("Error while attempting to get connection: " + ex);
            return null;
        }
    }

    public boolean addPlayer(String name) {
        JDCConnection conn = null;
        try {
            util.debug("Attempting to add player '" + name + "' to database");
            conn = getConnection();
            String statement = "INSERT IGNORE INTO `" + config.DbPlayerTable + "` (`player_id`, `player`) VALUES (NULL, '" + name + "');";
            conn.createStatement().execute(statement);
        } catch (SQLException ex) {
            util.severe("Unable to add player to database: " + ex);
            return false;
        } finally {
            conn.close();
        }
        if (!updateDbLists()) {
            return false;
        }
        return true;
    }

    private boolean updateDbLists() {
        JDCConnection conn = null;
        Statement stmnt = null;
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            ResultSet res = stmnt.executeQuery("SELECT * FROM `" + config.DbPlayerTable + "`;");
            while (res.next()) {
                dbPlayers.put(res.getString("player"), res.getInt("player_id"));
            }
        } catch (SQLException ex) {
            util.severe("Unable to update local data lists from database: " + ex);
            return false;
        } finally {
            try {
                if (stmnt != null) {
                    stmnt.close();
                }
                conn.close();
            } catch (SQLException ex) {
                util.severe("unable to close SQL connection: " + ex);
            }
        }
        return true;
    }

    private boolean checkTables() {
        JDCConnection conn = null;
        Statement stmnt = null;
        try {
            conn = getConnection();
            stmnt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();

            //Check if tables exist
            if (!JDBCUtil.tableExists(dbm, config.DbPlayerTable)) {
                util.info("table `" + config.DbPlayerTable + "` not found, creating...");
                stmnt.execute("CREATE TABLE IF NOT EXISTS `" + config.DbPlayerTable + "` (`player_id` int(11) NOT NULL AUTO_INCREMENT, `player` varchar(255) NOT NULL, PRIMARY KEY (`player_id`), UNIQUE KEY `player` (`player`) );");
            }
            if (!JDBCUtil.tableExists(dbm, config.DbKillTable)) {
                util.info("Table `" + config.DbKillTable + "` not found, creating...");
                stmnt.execute("CREATE TABLE IF NOT EXISTS `" + config.DbKillTable + "` (`id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `victim_id` int(11) NOT NULL, `killer_id` int(11) NOT NULL, `map` varchar(255) NOT NULL, `gt` varchar(255) NOT NULL, PRIMARY KEY (`id`));");
            }
            if (!JDBCUtil.tableExists(dbm, config.DbWinTable)) {
                util.info("Table `" + config.DbWinTable + "` not found, creating...");
                stmnt.execute("CREATE TABLE IF NOT EXISTS `" + config.DbWinTable + "` (`id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `map` varchar(255) NOT NULL, `gt` varchar(255) NOT NULL, `win` boolean NOT NULL, PRIMARY KEY (`id`));");
            }
            if (!JDBCUtil.tableExists(dbm, config.DbGameTable)) {
                util.info("Table `" + config.DbGameTable + "` not found, creating...");
                stmnt.execute("CREATE TABLE IF NOT EXISTS `" + config.DbGameTable + "` (`id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `winner` varchar(255) NOT NULL, `map` varchar(255) NOT NULL, `gt` varchar(255) NOT NULL, PRIMARY KEY (`id`));");
            }
        } catch (SQLException ex) {
            util.severe("Error checking PVP tables: " + ex);
            return false;
        } finally {
            try {
                if (stmnt != null) {
                    stmnt.close();
                }
                conn.close();
            } catch (SQLException ex) {
                util.severe("Unable to close SQL connection: " + ex);
            }
        }
        return true;
    }

    @Override
    public void run() {
        if (Killqueue.isEmpty() && Joinqueue.isEmpty() && Gamequeue.isEmpty()) {
            return;
        }
        JDCConnection conn = getConnection();
        PreparedStatement stmnt = null;
        try {
            while (!Gamequeue.isEmpty()) {
                GameEntry entry = Gamequeue.poll();
                util.debug("Winner: " + entry.getWinner());
                stmnt = conn.prepareStatement("INSERT into `" + config.DbGameTable + "` (date, winner, map, gt) VALUES (?, ?, ?, ?);");
                stmnt.setString(1, entry.getDate());
                stmnt.setString(2, entry.getWinner());
                stmnt.setString(3, entry.getMap());
                stmnt.setString(4, entry.getGt());
                stmnt.executeUpdate();
                stmnt.close();
            }
            while (!Joinqueue.isEmpty()) {
                JoinEntry entry = Joinqueue.poll();
                util.debug("Player: " + entry.getPlayer());
                util.debug("Win: " + entry.getWin());
                if (!dbPlayers.containsKey(entry.getPlayer()) && !addPlayer(entry.getPlayer())) {
                    util.debug("Player `" + entry.getPlayer() + "` not found, skipping entry");
                    continue;
                }
                if (dbPlayers.get(entry.getPlayer()) == null) {
                    util.debug("No player found in hashmap, skipping entry");
                    continue;
                }
                if (entry.getPlayer() == null) {
                    util.debug("No player found in entry, skipping");
                }
                stmnt = conn.prepareStatement("INSERT into `" + config.DbWinTable + "` (date, player_id, map, gt, win) VALUES (?, ?, ?, ?, ?);");
                stmnt.setString(1, entry.getDate());
                stmnt.setInt(2, dbPlayers.get(entry.getPlayer()));
                stmnt.setString(3, entry.getMap());
                stmnt.setString(4, entry.getGt());
                stmnt.setBoolean(5, entry.getWin());
                stmnt.executeUpdate();
                stmnt.close();
            }
            while (!Killqueue.isEmpty()) {
                KillEntry entry = Killqueue.poll();
                util.debug("Killer: " + entry.getKiller());
                util.debug("Victim: " + entry.getVictim());
                if (!dbPlayers.containsKey(entry.getKiller()) && !addPlayer(entry.getKiller())) {
                    util.debug("Killer '" + entry.getKiller() + "' not found, skipping entry");
                    continue;
                }
                if (dbPlayers.get(entry.getKiller()) == null) {
                    util.debug("No Killer found in hashmap, skipping entry");
                    continue;
                }
                if (entry.getKiller() == null) {
                    util.debug("No killer found in entry, skipping");
                    continue;
                }

                if (!dbPlayers.containsKey(entry.getVictim()) && !addPlayer(entry.getVictim())) {
                    util.debug("Victim '" + entry.getVictim() + "' not found, skipping entry");
                    continue;
                }
                if (entry.getVictim() == null) {
                    util.debug("No Victim found in entry, skipping");
                    continue;
                }
                if (dbPlayers.get(entry.getVictim()) == null) {
                    util.debug("No Victim found in hashmap, skipping entry");
                    continue;
                }
                stmnt = conn.prepareStatement("INSERT into `" + config.DbKillTable + "` (date, victim_id, killer_id, map, gt) VALUES (?, ?, ?, ?, ?);");
                stmnt.setString(1, entry.getDate());
                stmnt.setInt(2, dbPlayers.get(entry.getVictim()));
                stmnt.setInt(3, dbPlayers.get(entry.getKiller()));
                stmnt.setString(4, entry.getMap());
                stmnt.setString(5, entry.getGt());
                stmnt.executeUpdate();
                stmnt.close();
            }
        } catch (Exception ex) {
            util.severe("Exception: " + ex);
        }
    }
}
