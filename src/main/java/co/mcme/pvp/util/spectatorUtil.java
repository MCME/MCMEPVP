package co.mcme.pvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import co.mcme.pvp.MCMEPVP;

public class spectatorUtil {
	
	public static void setSpectator(Player p){
		for(Player q : Bukkit.getOnlinePlayers()){
			String statusq = teamUtil.getPlayerTeam(q);
			if(!q.equals(p)){
				if(!statusq.equals("spectator")){
					q.hidePlayer(p);
				} else{
					q.showPlayer(p);
					p.showPlayer(q);
				}
			}
		}
		isSpectator(p);
		if (MCMEPVP.GameStatus == 1) {
			MCMEPVP.CurrentGame.addSpectatorTeam(p);
		}
	}
	
	public static void setParticipant(Player p){
		for(Player q : Bukkit.getOnlinePlayers()){
			q.showPlayer(p);
		}
		notSpectator(p);
	}
	
	public static void startingSpectators(){
    	for(Player p : Bukkit.getOnlinePlayers()){
    		String status = teamUtil.getPlayerTeam(p);
    		if(status.equals("spectator")){
    			spectatorUtil.setSpectator(p);
    			Vector vec = MCMEPVP.Spawns.get("spectator");
    			Location loc = new Location(MCMEPVP.PVPWorld, vec.getX(), vec.getY() + 0.5, vec.getZ());
    			p.teleport(loc);
    		}
    	}
    }

	public static void isSpectator(Player p){
		p.setAllowFlight(true);
		p.setCanPickupItems(false);
	}
	
	public static void notSpectator(Player p){
		p.setAllowFlight(false);
		p.setCanPickupItems(true);
	}
	
	public static void showAll(Player p){
		for(Player q : Bukkit.getOnlinePlayers()){
			p.showPlayer(q);
			q.showPlayer(p);
		}
		p.setAllowFlight(false);
		p.setCanPickupItems(true);
	}

}
