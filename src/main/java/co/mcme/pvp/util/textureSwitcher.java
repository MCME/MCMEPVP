package co.mcme.pvp.util;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.PVPMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class textureSwitcher {

    private static Configuration config;
    private static String Texturepack;
    private static String Region;

    public static String getTexturePack() {
        config = Bukkit.getPluginManager().getPlugin("MCMEPVP").getConfig();
        if (MCMEPVP.GameStatus == 0) {
            Texturepack = config.getString("textures.eriador");
            return Texturepack;
        }
        if (MCMEPVP.GameStatus == 1) {
            if (config.contains((PVPMap.toLowerCase() + ".region"))) {
                Region = config.getString(PVPMap.toLowerCase() + ".region");
                Texturepack = config.getString("textures." + Region.toLowerCase());
                return Texturepack;
            } else {
                Texturepack = config.getString("textures.eriador");
                return Texturepack;
            }
        } else {
            Texturepack = config.getString("textures.eriador");
            return Texturepack;
        }
    }

    public static void switchTP(Player p) {
        p.setTexturePack(getTexturePack());
    }
}
