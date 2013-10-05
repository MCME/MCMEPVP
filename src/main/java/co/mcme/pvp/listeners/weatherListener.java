package co.mcme.pvp.listeners;

import co.mcme.pvp.MCMEPVP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class weatherListener implements Listener {

    @EventHandler
    public void weatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            if (MCMEPVP.GameStatus == 1) {
                if (MCMEPVP.PVPMap.equals("HelmsDeep")) {
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
