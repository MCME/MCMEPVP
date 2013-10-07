package co.mcme.pvp.classes;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public abstract class pvpClass {
    
    public abstract String getName();
    public abstract ItemStack getItemForUI();
    public abstract ArrayList<String> getDescription();
    public abstract pvpLoadout getLoadout();
}
