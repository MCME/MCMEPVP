package co.mcme.pvp.commands;

import co.mcme.pvp.MCMEPVP;
import static co.mcme.pvp.MCMEPVP.CurrentLobby;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class voteCmdMethods {

    static ChatColor prim = ChatColor.GREEN;
    static ChatColor scd = ChatColor.DARK_AQUA;
    static ChatColor err = ChatColor.GRAY;
    public static HashSet<String> hasVoted = new HashSet<String>();

    public static void pvpVoteInfo(Player p) {
        if (p.hasPermission("mcmepvp.vote")) {
            CurrentLobby.getVoteMaps(p);
        } else {
            nope(p);
        }
    }

    public static void pvpVote(Player p, String a) {
        if (p.hasPermission("mcmepvp.vote")) {
            if (a.equals("1") || a.equals("2")) {
                if (hasVoted.contains(p.getName())) {
                    p.sendMessage(err + "You have already voted!");
                    return;
                } else {
                    hasVoted.add(p.getName());
                    int i = Integer.valueOf(a);
                    CurrentLobby.voteMap(i);
                    p.sendMessage(prim + "You have voted for map " + i + "!");
                    return;
                }
            }
            if (a.equals("on")) {
                if (p.hasPermission("mcmepvp.auto")) {
                    if (!MCMEPVP.voteMap) {
                        MCMEPVP.voteMap = true;
                        CurrentLobby.setMapVote();
                        p.sendMessage(prim + "Map voting enabled!");
                        return;
                    } else {
                        p.sendMessage(err + "Map voting is already enabled!");
                        return;
                    }
                } else {
                    nope(p);
                    return;
                }
            }
            if (a.equals("off")) {
                if (p.hasPermission("mcmepvp.auto")) {
                    if (MCMEPVP.voteMap) {
                        MCMEPVP.voteMap = false;
                        p.sendMessage(scd + "Map voting disabled!");
                        hasVoted.clear();
                    } else {
                        p.sendMessage(err + "Map voting is already disabled!");
                    }
                } else {
                    nope(p);
                }
            } else {
                p.sendMessage(MCMEPVP.negativecolor + a + " is not a valid option!");
            }
        } else {
            nope(p);
        }
    }

    private static void nope(Player p) {
        p.sendMessage(MCMEPVP.negativecolor + "You do not have permission to do that!");
    }
}
