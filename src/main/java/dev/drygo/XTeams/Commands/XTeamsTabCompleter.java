package dev.drygo.XTeams.Commands;

import dev.drygo.XTeams.Managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import dev.drygo.XTeams.Models.Team;
import dev.drygo.XTeams.Utils.ChatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class XTeamsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        String teamNameMessage = ChatUtils.getMessage("tab_complete.create.team", (Player) sender);
        String priorityMessage = ChatUtils.getMessage("tab_complete.create.priority", (Player) sender);
        String displayNameMessage = ChatUtils.getMessage("tab_complete.setdisplay.display_name", (Player) sender);
        // Primero comprobamos si el jugador tiene el permiso para el subcomando
        if (args.length == 0) {
            return Collections.emptyList();  // No hay nada que autocompletar si no hay argumentos
        }

        // Lista de subcomandos disponibles
        List<String> subCommands = new ArrayList<>();

        if (sender.hasPermission("xteams.command.create") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("create");
        if (sender.hasPermission("xteams.command.delete") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("delete");
        if (sender.hasPermission("xteams.command.join") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("join");
        if (sender.hasPermission("xteams.command.leave") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("leave");
        if (sender.hasPermission("xteams.command.setdisplay") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("setdisplay");
        if (sender.hasPermission("xteams.command.info") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("info");
        if (sender.hasPermission("xteams.command.teaminfo") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("teaminfo");
        if (sender.hasPermission("xteams.command.playerinfo") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("playerinfo");
        if (sender.hasPermission("xteams.command.sync") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("sync");
        if (sender.hasPermission("xteams.command.reload") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("reload");
        if (sender.hasPermission("xteams.command.list") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("list");
        if (sender.hasPermission("xteams.command.help") || sender.hasPermission("xteams.admin") || sender.isOp()) subCommands.add("help");

        if (args.length == 1) {
            return getMatches(args[0], subCommands);
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length == 2) {
                    return Collections.singletonList(teamNameMessage);
                }
                if (args.length == 3) {
                    return Collections.singletonList(priorityMessage);
                }
            }
            case "delete" -> {
                if (args.length == 2) {
                    List<String> teams = getTeamsList();
                    return getMatches(args[1], teams);
                }
            }
            case "setdisplay" -> {
                if (args.length == 2) {
                    List<String> teams = getTeamsList();
                    return getMatches(args[1], teams);
                } else if (args.length == 3) {
                    return Collections.singletonList("\"" + displayNameMessage + "\"");
                }
            }
            case "join", "leave" -> {
                if (args.length == 2) {
                    return getMatches(args[1], getTeamsListWithStar());
                }
                if (args.length == 3) {
                    return getMatches(args[2], getPlayerSelectorsAndList());
                }
            }
            case "info", "list", "help", "sync" -> {
                return Collections.emptyList();
            }
            case "teaminfo" -> {
                if (args.length == 2) {
                    return getMatches(args[1], getTeamsList());
                }
            }
            case "playerinfo" -> {
                if (args.length == 2) {
                    return getMatches(args[1], getPlayersList());
                }
            }
            default -> {
            }
        }

        return Collections.emptyList();
    }

    private List<String> getMatches(String arg, List<String> options) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(arg.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }

    private List<String> getTeamsListWithStar() {
        List<String> teams = getTeamsList();
        teams.add("*");
        return teams;
    }

    private List<String> getPlayersListWithStar() {
        List<String> players = getPlayersList();
        players.add("*");
        return players;
    }

    private List<String> getTeamsList() {
        List<String> teams = new ArrayList<>();
        Set<Team> allTeams = TeamManager.getAllTeams();
        if (!allTeams.isEmpty()) {
            for (Team team : allTeams) {
                teams.add(team.getName());
            }
        }
        return teams;
    }

    private List<String> getPlayersList() {
        Set<String> players = new HashSet<>(TeamManager.getAllPlayersInTeams());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            players.add(onlinePlayer.getName());
        }
        List<String> playerList = new ArrayList<>(players);
        playerList.sort(String::compareToIgnoreCase);
        return playerList;
    }

    private List<String> getPlayerSelectorsAndList() {
        List<String> options = new ArrayList<>();
        options.add("@online");
        options.add("@team");
        options.add("@teamall");
        options.add("@noteam");
        options.add("*");
        options.addAll(getPlayersList());
        return options;
    }
}
