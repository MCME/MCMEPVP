package co.mcme.pvp.stats;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.gameType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import java.math.BigDecimal;
import java.util.HashMap;
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
                    playerStats.get(victim.getName()).incrementDeaths(1);
                    playerStats.get(killer.getName()).incrementKills(1);
                }
            } else {
                Player victim = event.getEntity();
                HashMap<String, PlayerStat> playerStats = MCMEPVP.CurrentGame.getPlayerStats();
                if (playerStats.containsKey(victim.getName())) {
                    playerStats.get(victim.getName()).incrementDeaths(1);
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
            boolean won = getPlayerTeam(target).equals(winner);
            BasicDBObject query = new BasicDBObject("name", target.getName());
            DBCursor cursor = Database.getPlayerCollection().find(query);
            if (cursor.hasNext()) {
                try {
                    BasicDBObject prev = (BasicDBObject) cursor.next();
                    int newkills = prev.getInt("kills") + ps.getKills();
                    int newdeaths = prev.getInt("deaths") + ps.getDeaths();
                    double newkd = newkills / newdeaths;
                    BasicDBObject prevgames = (BasicDBObject) prev.get("games");
                    int newgamesplayed = prevgames.getInt("played") + 1;
                    int newgameswon = prevgames.getInt("won");
                    if (won) {
                        newgameswon += 1;
                    }
                    int newwinperc = newgameswon / newgamesplayed;
                    BasicDBObject newobj = new BasicDBObject()
                            .append("name", target.getName())
                            .append("kills", newkills)
                            .append("deaths", newdeaths)
                            .append("kd", newkd)
                            .append("games", new BasicDBObject()
                            .append("played", newgamesplayed)
                            .append("won", newgameswon)
                            .append("winPercentage", round(newwinperc, 2)));
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
                BasicDBObject newobj = new BasicDBObject()
                        .append("name", target.getName())
                        .append("kills", ps.getKills())
                        .append("deaths", ps.getDeaths())
                        .append("kd", ps.getKills() / ps.getDeaths())
                        .append("games", new BasicDBObject()
                        .append("played", 1)
                        .append("won", gameswon)
                        .append("winPercentage", round(gameswon / 1, 2)));
                Database.getPlayerCollection().insert(newobj);
            }
        }
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
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
//    }
//}