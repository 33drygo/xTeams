package dev.drygo.XTeams.Utils;

import dev.drygo.XTeams.Managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerSelector {
    public static Set<String> resolve(String selector) {
        return switch (selector.toLowerCase()) {
            case "*", "@teamall" -> TeamManager.getAllPlayersInTeams();
            case "@online" -> getOnlinePlayerNames();
            case "@team" -> getOnlinePlayersInTeam();
            case "@noteam" -> getOnlinePlayersWithoutTeam();
            default -> {
                Set<String> single = new HashSet<>();
                single.add(selector);
                yield single;
            }
        };
    }

    public static boolean isSelector(String arg) {
        return arg.equals("*") || arg.startsWith("@");
    }

    private static Set<String> getOnlinePlayerNames() {
        Set<String> names = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }

    private static Set<String> getOnlinePlayersInTeam() {
        Set<String> inTeams = TeamManager.getAllPlayersInTeams();
        Set<String> result = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (inTeams.contains(player.getName())) {
                result.add(player.getName());
            }
        }
        return result;
    }

    private static Set<String> getOnlinePlayersWithoutTeam() {
        Set<String> result = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!TeamManager.isInAnyTeam(player.getName())) {
                result.add(player.getName());
            }
        }
        return result;
    }
}