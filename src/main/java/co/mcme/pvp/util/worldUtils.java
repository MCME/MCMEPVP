package co.mcme.pvp.util;

import java.util.ArrayList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class worldUtils {

    private static ArrayList<EntityType> toRemove = new ArrayList();

    public static int removeEntities(ArrayList<EntityType> types) {
        toRemove = types;
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
