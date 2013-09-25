package co.mcme.pvp.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public abstract class lobbyType {

    public lobbyType() {
        //TODO: Code on Game Start
    }
    
    public abstract void autoRun();

    public abstract void onPlayerJoin(PlayerJoinEvent event);
    
    public abstract void onPlayerleaveServer(PlayerQuitEvent event);

    public abstract void setTeam(Player player, String Team);
    
    public abstract void setMapVote();
    
    public abstract void voteMap(Integer i);
    
    public abstract String randomMap();
    
    public abstract boolean checkFlags();
    
    public abstract String randomGameType();
    
    public abstract void gameScore();
    
    public abstract void autoRunTimer();
    
    public abstract void stopLobby();

    public abstract void onRespawn(PlayerRespawnEvent event);

    public abstract void clearBoard();

    public abstract void displayBoard();

	public abstract void getVoteMaps(Player p);
	
}
