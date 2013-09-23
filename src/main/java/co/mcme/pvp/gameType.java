package co.mcme.pvp;

import java.util.HashMap;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class gameType {

    public gameType() {
        //TODO: Code on Game Start
    }

    public abstract void claimLootSign(Sign sign);

    @Deprecated
    public abstract void onPlayerLogin(PlayerLoginEvent event);

    public abstract void onPlayerleaveServer(PlayerQuitEvent event);

    public abstract void onPlayerJoin(PlayerJoinEvent event);

    public abstract void addPlayerDuringGame(Player player);

    public abstract void onPlayerdie(PlayerDeathEvent event);

    public abstract void addTeam(Player player, String Team);
    
    public abstract void addSpectatorTeam(Player player);

    public abstract void onPlayerhit(EntityDamageByEntityEvent event);

    public abstract void onPlayerShoot(EntityDamageByEntityEvent event);

    public abstract void onRespawn(PlayerRespawnEvent event);

    public abstract int team1count();

    public abstract int team2count();

    public abstract String team1();

    public abstract String team2();

    public abstract Scoreboard getBoard();
    
    public abstract Objective getObjective();

    public abstract void clearBoard();

    public abstract void displayBoard();

    public abstract HashMap<?, ?> getPlaying();

    public abstract boolean isJoinable();
    
    public abstract boolean allowBlockBreak();
    
    public abstract boolean allowBlockPlace();
    
    public abstract boolean allowContainerIteraction();
    
    public abstract boolean allowExplosionLogging();
    
    public abstract boolean allowCustomAttributes();
}