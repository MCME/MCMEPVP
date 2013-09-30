package co.mcme.pvp.util;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class worldUtils {

    public static int removeEntities(List<EntityType> types) {
        int num = 0;
        for (Entity ent : config.PVPWorld.getEntities()) {
            if (types.contains(ent.getType())) {
                ent.remove();
                num++;
            }
        }
        return num;
    }
}
