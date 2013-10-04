package co.mcme.pvp.stats;

import co.mcme.pvp.MCMEPVP;
import co.mcme.pvp.util.config;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import org.bukkit.Server;

public final class Database {

    private static MCMEPVP inst;
    private static Server s;
    private static MongoClient mongoClient;
    private static boolean auth;
    private static DB db;
    private static DBCollection games;
    private static DBCollection players;

    public Database(Server server, MCMEPVP plugin) throws UnknownHostException {
        s = server;
        inst = plugin;
        mongoClient = new MongoClient("localhost");
        db = mongoClient.getDB("pvp");
        auth = db.authenticate(config.mongoUser, config.mongoPassword.toCharArray());
        loadCollections();
    }

    public void loadCollections() {
        games = db.getCollection("games");
        players = db.getCollection("players");
    }

    public boolean isAuthed() {
        return auth;
    }

    public static DB getDB() {
        return db;
    }

    public static DBCollection getGameCollection() {
        return games;
    }

    public static DBCollection getPlayerCollection() {
        return players;
    }
}
