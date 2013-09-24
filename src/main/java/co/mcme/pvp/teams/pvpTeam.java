package co.mcme.pvp.teams;

import co.mcme.pvp.util.armorColor;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class pvpTeam {

    private String name;
    private ChatColor chatColor;
    private armorColor armorColor;
    private ArrayList<String> members = new ArrayList<String>();

    public pvpTeam(String n, armorColor ac, ChatColor cc) {
        this.name = n;
        this.armorColor = ac;
        this.chatColor = cc;
    }

    public String getName() {
        return name;
    }

    public void setName(String newname) {
        this.name = newname;
    }

    public armorColor getArmorColor() {
        return armorColor;
    }

    public void setArmorColor(armorColor newac) {
        this.armorColor = newac;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setChatColor(ChatColor newcc) {
        this.chatColor = newcc;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public boolean addPlayerToTeam(Player p) {
        if (!members.contains(p.getName())) {
            members.add(p.getName());
            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayerFromTeam(Player p) {
        if (members.contains(p.getName())) {
            members.remove(p.getName());
            return true;
        } else {
            return false;
        }
    }
}
