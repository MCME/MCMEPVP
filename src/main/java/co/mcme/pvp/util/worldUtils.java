package co.mcme.pvp.util;

import java.util.ArrayList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class worldUtils {

    public static int removeEntities(ArrayList<EntityType> types) {
        ArrayList<EntityType> toRemove = types;
        int num = 0;
        for (Entity ent : config.PVPWorld.getEntities()) {
            if (toRemove.contains(ent.getType())) {
                ent.remove();
                num++;
            }
        }
        return num;
    }
}
