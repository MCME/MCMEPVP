package co.mcme.pvp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import co.mcme.pvp.MCMEPVP;

public class weatherListener implements Listener{
	
	@EventHandler
	public void weatherChange(WeatherChangeEvent event){
		if (event.toWeatherState()) {
			if (MCMEPVP.GameStatus == 1) {
				if (MCMEPVP.CurrentMap.getName().equals("HelmsDeep")) {
					event.setCancelled(false);
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

}
