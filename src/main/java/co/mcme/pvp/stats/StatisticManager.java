package co.mcme.pvp.stats;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import co.mcme.pvp.util.util;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.types.BasicBSONList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatisticManager {

    static HashMap<String, String> lastStatus;
    static gameType lastGame;

    public static void storePlayerDeath(PlayerDeathEvent event) {
        if (!MCMEPVP.debug) {
            if (event.getEntity().getKiller() instanceof Player) {
                Player victim = event.getEntity();
                Player killer = event.getEntity().getKiller();
                HashMap<String, PlayerStat> playerStats = MCMEPVP.CurrentGame.getPlayerStats();
                if (playerStats.containsKey(victim.getName()) && playerStats.containsKey(killer.getName())) {
                    playerStats.get(victim.getName()).addDeath(new PvpDeath(victim.getName(), killer.getName(), MCMEPVP.PVPMap, killer.getItemInHand().getType(), MCMEPVP.PVPGT));
                    playerStats.get(killer.getName()).addKill(new PvpDeath(victim.getName(), killer.getName(), MCMEPVP.PVPMap, killer.getItemInHand().getType(), MCMEPVP.PVPGT));
                }
            } else {
                Player victim = event.getEntity();
                Location loc = victim.getLocation();
                loc.setY(loc.getY() - 1);
                HashMap<String, PlayerStat> playerStats = MCMEPVP.CurrentGame.getPlayerStats();
                if (playerStats.containsKey(victim.getName())) {
                    playerStats.get(victim.getName()).addDeath(new PvpDeath(victim.getName(), "$$", MCMEPVP.PVPMap, loc.getBlock().getType(), MCMEPVP.PVPGT));
                }
            }
        }
    }

    public static void logGame(String winner) {
        if (!MCMEPVP.debug) {
            lastGame = MCMEPVP.CurrentGame;
            lastStatus = MCMEPVP.PlayerStatus;
            pushPlayerStats(winner);
        }
    }

    private static void pushPlayerStats(String winner) {
        HashMap<String, PlayerStat> playerStats = lastGame.getPlayerStats();
        for (PlayerStat ps : playerStats.values()) {
            Player target = ps.getPlayer();
            boolean won;
            if (winner.contains(",")) {
                won = winner.contains(target.getName());
            } else {
                won = getPlayerTeam(target).equals(winner) || target.getName().equals(winner);
            }
            BasicDBObject query = new BasicDBObject("name", target.getName());
            DBCursor cursor = Database.getPlayerCollection().find(query);
            if (cursor.hasNext()) {
                try {
                    BasicDBObject prev = (BasicDBObject) cursor.next();
                    int newkills = prev.getInt("kills") + ps.getKillCount();
                    int newdeaths = prev.getInt("deaths") + ps.getDeathCount();
                    ArrayList<String> newrdeaths = (ArrayList) ((BasicBSONList) prev.get("recentdeaths"));
                    for (PvpDeath death : ps.getDeaths()) {
                        newrdeaths.add(death.getKiller() + "|" + death.getWeapon() + "|" + death.getMap() + "|" + death.getGameType());
                    }
                    ArrayList<String> newrkills = (ArrayList) ((BasicBSONList) prev.get("recentkills"));
                    for (PvpDeath kill : ps.getKills()) {
                        newrkills.add(kill.getVictim() + "|" + kill.getWeapon() + "|" + kill.getMap() + "|" + kill.getGameType());
                    }
                    double newkd = newkills / newdeaths;
                    BasicDBObject prevgames = (BasicDBObject) prev.get("games");
                    int newgamesplayed = prevgames.getInt("played") + 1;
                    int newgameswon = prevgames.getInt("won");
                    if (won) {
                        newgameswon += 1;
                    }
                    double newwinperc = newgameswon / newgamesplayed;
                    BasicDBObject newobj = new BasicDBObject()
                            .append("name", target.getName())
                            .append("kills", newkills)
                            .append("deaths", newdeaths)
                            .append("kd", newkd)
                            .append("games", new BasicDBObject()
                            .append("played", newgamesplayed)
                            .append("won", newgameswon)
                            .append("winPercentage", newwinperc))
                            .append("recentkills", newrkills)
                            .append("recentdeaths", newrdeaths);
                    Database.getPlayerCollection().update(prev, newobj);
                } finally {
                    cursor.close();
                }
            } else {
                //Create new document
                int gameswon = 0;
                if (won) {
                    gameswon = 1;
                }
                ArrayList<String> newrdeaths = new ArrayList();
                for (PvpDeath death : ps.getDeaths()) {
                    newrdeaths.add(death.getKiller() + "|" + death.getWeapon() + "|" + death.getMap() + "|" + death.getGameType());
                }
                ArrayList<String> newrkills = new ArrayList();
                for (PvpDeath kill : ps.getKills()) {
                    newrkills.add(kill.getVictim() + "|" + kill.getWeapon() + "|" + kill.getMap() + "|" + kill.getGameType());
                }
                util.info(String.valueOf(ps.getDeathCount()));
                BasicDBObject newobj = new BasicDBObject()
                        .append("name", target.getName())
                        .append("kills", ps.getKillCount())
                        .append("deaths", ps.getDeathCount())
                        .append("kd", (double) (ps.getKillCount() / ps.getDeathCount()))
                        .append("games", new BasicDBObject()
                        .append("played", 1)
                        .append("won", gameswon)
                        .append("winPercentage", (double) (gameswon / 1)))
                        .append("recentkills", newrkills)
                        .append("recentdeaths", newrdeaths);
                Database.getPlayerCollection().insert(newobj);
            }
        }
    }

    public static String getPlayerTeam(Player player) {
        String status = "spectator";
        if (lastStatus.containsKey(player.getName())) {
            status = lastStatus.get(player.getName());
        }
        return status;
    }
}
// What a player json file looks like
//{
//    name: "meggawatts",
//    kills: 1508,
//    deaths: 1013,
//    kd: 1.49,
//    games: {
//        played: 359,
//        won: 175,
//        winPercentage: 0.48
//    },
//    recentkills: [
//        "victim|weapon|map|gametype",
//        "victim|weapon|map|gametype"
//    ],
//    recentdeaths: [
//        "killer|weapon|map|gametype",
//        "killer|weapon|map|gametype"
//    ]
//}