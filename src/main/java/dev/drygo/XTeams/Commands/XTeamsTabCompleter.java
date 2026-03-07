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
        String teamNameMessage    = ChatUtils.getMessage("tab_complete.create.team", null);
        String priorityMessage    = ChatUtils.getMessage("tab_complete.create.priority", null);
        String displayNameMessage = ChatUtils.getMessage("tab_complete.setdisplay.display_name", null);

        if (args.length == 0) return Collections.emptyList();

        // Subcomandos según permisos
        List<String> subCommands = new ArrayList<>();
        if (hasPerm(sender, "xteams.command.create"))      subCommands.add("create");
        if (hasPerm(sender, "xteams.command.delete"))      subCommands.add("delete");
        if (hasPerm(sender, "xteams.command.join"))        subCommands.add("join");
        if (hasPerm(sender, "xteams.command.leave"))       subCommands.add("leave");
        if (hasPerm(sender, "xteams.command.setdisplay"))  subCommands.add("setdisplay");
        if (hasPerm(sender, "xteams.command.info"))        subCommands.add("info");
        if (hasPerm(sender, "xteams.command.teaminfo"))    subCommands.add("teaminfo");
        if (hasPerm(sender, "xteams.command.playerinfo"))  subCommands.add("playerinfo");
        if (hasPerm(sender, "xteams.command.sync"))        subCommands.add("sync");
        if (hasPerm(sender, "xteams.command.reload"))      subCommands.add("reload");
        if (hasPerm(sender, "xteams.command.list"))        subCommands.add("list");
        if (hasPerm(sender, "xteams.command.help"))        subCommands.add("help");

        if (args.length == 1) {
            return getMatches(args[0], subCommands);
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length == 2) return Collections.singletonList(teamNameMessage);
                if (args.length == 3) return Collections.singletonList(priorityMessage);
            }
            case "delete" -> {
                if (args.length == 2) return getMatches(args[1], getTeamsListWithStar());
            }
            case "setdisplay" -> {
                if (args.length == 2) return getMatches(args[1], getTeamsList());
                if (args.length == 3) return Collections.singletonList("\"" + displayNameMessage + "\"");
            }
            case "join", "leave" -> {
                if (args.length == 2) return getMatches(args[1], getTeamsListWithStar());
                if (args.length == 3) return getMatches(args[2], getPlayerSelectorsAndList());
            }
            case "info", "list", "help", "sync" -> {
                return Collections.emptyList();
            }
            case "teaminfo" -> {
                if (args.length == 2) return getMatches(args[1], getTeamsList());
            }
            case "playerinfo" -> {
                // Muestra nicknames visibles (resueltos), nunca UUIDs
                if (args.length == 2) return getMatches(args[1], getPlayersList());
            }
        }

        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Helpers de permisos
    // -------------------------------------------------------------------------

    private boolean hasPerm(CommandSender sender, String perm) {
        return sender.hasPermission(perm)
                || sender.hasPermission("xteams.admin")
                || sender.isOp();
    }

    // -------------------------------------------------------------------------
    // Helpers de listas
    // -------------------------------------------------------------------------

    private List<String> getMatches(String arg, List<String> options) {
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase().startsWith(arg.toLowerCase())) {
                matches.add(option);
            }
        }
        return matches;
    }

    private List<String> getTeamsList() {
        List<String> teams = new ArrayList<>();
        for (Team team : TeamManager.getAllTeams()) {
            teams.add(team.getName());
        }
        teams.sort(String::compareToIgnoreCase);
        return teams;
    }

    private List<String> getTeamsListWithStar() {
        List<String> teams = getTeamsList();
        teams.add("*");
        return teams;
    }

    private List<String> getPlayersList() {

        Set<String> players = new LinkedHashSet<>(TeamManager.getAllPlayerNamesInTeams());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            players.add(onlinePlayer.getName());
        }

        List<String> list = new ArrayList<>(players);
        list.sort(String::compareToIgnoreCase);
        return list;
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