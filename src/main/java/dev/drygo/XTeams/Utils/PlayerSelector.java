package dev.drygo.XTeams.Utils;

import dev.drygo.XTeams.Managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerSelector {

    public static Set<String> resolve(String selector) {
        return switch (selector.toLowerCase()) {
            // Devuelve los nicknames de TODOS los jugadores en algún equipo
            case "*", "@teamall" -> TeamManager.getAllPlayerNamesInTeams();
            // Devuelve los nicknames de los jugadores online
            case "@online" -> getOnlinePlayerNames();
            // Devuelve los nicknames de los jugadores online que están en algún equipo
            case "@team" -> getOnlinePlayersInTeam();
            // Devuelve los nicknames de los jugadores online que NO están en ningún equipo
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

    // -------------------------------------------------------------------------
    // Helpers — todos devuelven nicknames visibles, nunca IDs internos
    // -------------------------------------------------------------------------

    private static Set<String> getOnlinePlayerNames() {
        Set<String> names = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }

    private static Set<String> getOnlinePlayersInTeam() {
        Set<String> result = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            // isInAnyTeam recibe nickname y hace la conversión interna
            if (TeamManager.isInAnyTeam(player.getName())) {
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