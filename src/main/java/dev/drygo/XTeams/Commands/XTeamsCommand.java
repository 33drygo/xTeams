package dev.drygo.XTeams.Commands;

import dev.drygo.XTeams.Hooks.AutoTeam.Managers.AutoTeamManager;
import dev.drygo.XTeams.Hooks.LuckPerms.Managers.LuckPermsGroupManager;
import dev.drygo.XTeams.Hooks.Minecraft.Managers.MinecraftTeamManager;
import dev.drygo.XTeams.Managers.TeamManager;
import dev.drygo.XTeams.Utils.PlayerSelector;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import dev.drygo.XTeams.Managers.ConfigManager;
import dev.drygo.XTeams.Models.Team;
import dev.drygo.XTeams.Utils.ChatUtils;
import dev.drygo.XTeams.XTeams;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class XTeamsCommand implements CommandExecutor {
    private final XTeams plugin;

    public XTeamsCommand(XTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.unknown_command", null));
            return false;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        switch (subcommand) {
            case "create" -> {
                if (dontHavePermission(sender, "xteams.command.create")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleCreate(sender, args);
            }
            case "delete" -> {
                if (dontHavePermission(sender, "xteams.command.delete")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleDelete(sender, args);
            }
            case "setdisplay" -> {
                if (dontHavePermission(sender, "xteams.command.setdisplay")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleSetDisplay(sender, args);
            }
            case "list" -> {
                if (dontHavePermission(sender, "xteams.command.list")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleList(sender);
            }
            case "join" -> {
                if (dontHavePermission(sender, "xteams.command.join")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleJoin(sender, args);
            }
            case "teaminfo" -> {
                if (dontHavePermission(sender, "xteams.command.teaminfo")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleInfoTeam(sender, args);
            }
            case "playerinfo" -> {
                if (dontHavePermission(sender, "xteams.command.playerinfo")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleInfoPlayer(sender, args);
            }
            case "leave" -> {
                if (dontHavePermission(sender, "xteams.command.leave")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleLeave(sender, args);
            }
            case "reload" -> {
                if (dontHavePermission(sender, "xteams.command.reload")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleReload(sender);
            }
            case "info" -> {
                if (dontHavePermission(sender, "xteams.command.info")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleInfo(sender);
            }
            case "sync" -> {
                if (dontHavePermission(sender, "xteams.command.sync")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                handleSync(sender);
                return false;
            }
            case "help" -> {
                if (dontHavePermission(sender, "xteams.command.help")) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.no_permission", null));
                    return true;
                }
                return handleHelp(sender);
            }
            default -> {
                sender.sendMessage(ChatUtils.getMessage("error.commands.unknown_command", null));
                return false;
            }
        }
    }

    private boolean handleCreate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_specified.create", null));
            return false;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.priority_not_specified", null));
            return false;
        }

        String teamName = args[1];
        int priority;

        try {
            priority = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.invalid_priority", null));
            return false;
        }

        Team team = TeamManager.getTeam(teamName);
        if (team != null) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_already_exists", null).replace("%team%", teamName));
            return false;
        }

        TeamManager.createTeam(teamName, teamName, priority, new HashSet<>());
        sender.sendMessage(ChatUtils.getMessage("commands.create.success", null).replace("%team%", teamName));

        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_specified.delete", null));
            return false;
        }

        String teamName = args[1];

        if (teamName.equals("*")) {
            if (dontHavePermission(sender, "xteams.command.delete.all")) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.all_not_permission.delete", null));
                return false;
            }
            if (TeamManager.getAllTeams().isEmpty()) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.anyteam_not_found", null));
                return false;
            }
            TeamManager.deleteAllTeams();
            sender.sendMessage(ChatUtils.getMessage("commands.delete.successall", null));
            return true;
        }

        Team team = TeamManager.getTeam(teamName);
        if (team == null) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null).replace("%team%", teamName));
            return false;
        }

        TeamManager.deleteTeam(team);
        sender.sendMessage(ChatUtils.getMessage("commands.delete.success", null).replace("%team%", teamName));
        return true;
    }

    private boolean handleSetDisplay(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.setdisplay.displayname_not_specified", null));
            return false;
        }

        String teamName = args[1];
        String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (!displayName.startsWith("\"") || !displayName.endsWith("\"")) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.setdisplay.invalid_format", null));
            return false;
        }

        displayName = displayName.substring(1, displayName.length() - 1);

        Team team = TeamManager.getTeam(teamName);
        if (team == null) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null).replace("%team%", teamName));
            return false;
        }

        team.setDisplayName(displayName);

        sender.sendMessage(ChatUtils.getMessage("commands.setdisplay.success", null).replace("%team%", teamName).replace("%display_name%", displayName));
        return true;
    }

    private boolean handleInfoTeam(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_specified.teaminfo", null));
            return false;
        }

        String teamName = args[1];
        Team team = TeamManager.getTeam (teamName);
        if (team == null) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null).replace("%team%", teamName));
            return false;
        }

        Map<String, Object> teamInfo = TeamManager.getTeamInfo(team);
        String displayName = (String) teamInfo.get("displayName");
        String name = (String) teamInfo.get("name");
        int priority = (int) teamInfo.get("priority");
        @SuppressWarnings("unchecked")
        Set<String> membersSet = (Set<String>) teamInfo.get("members");
        List<String> members = new ArrayList<>(membersSet);

        StringBuilder message = new StringBuilder();
        for (String line : ChatUtils.getMessageList("commands.teaminfo.string.header")) {
            line = line.replace("%prefix%", ConfigManager.getPrefix())
                    .replace("%team%", name)
                    .replace("%display_name%", displayName)
                    .replace("%priority%", String.valueOf(priority));
            line = ChatUtils.formatColor(line);
            line = PlaceholderAPI.setPlaceholders(null, line);
            message.append(line).append("\n");
        }

        if (members.isEmpty()) {
            message.append(ChatUtils.getMessage("commands.teaminfo.string.no_members", null)).append("\n");
        } else {
            message.append(ChatUtils.getMessage("commands.teaminfo.string.members_header", null)).append("\n");
            for (String member : members) {
                message.append(ChatUtils.getMessage("commands.teaminfo.string.members_row", null).replace("%member%", member)).append("\n");
            }
        }

        for (String line : ChatUtils.getMessageList("commands.teaminfo.string.footer")) {
            line = ChatUtils.formatColor(line);
            line = PlaceholderAPI.setPlaceholders(null, line);
            message.append(line).append("\n");
        }

        sender.sendMessage(message.toString());
        return true;
    }

    private boolean handleInfoPlayer(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.player_not_found", null).replace("%target%", sender.getName()));
            return false;
        }

        String targetName = args[1];

        List<Team> playerTeams = TeamManager.getPlayerTeams(targetName);

        if (playerTeams.isEmpty()) {
            sender.sendMessage(ChatUtils.getMessage("commands.playerinfo.string.no_teams", null).replace("%target%", targetName));
            return true;
        }

        playerTeams.sort(Comparator.comparingInt(Team::getPriority).reversed());
        Team mainTeam = playerTeams.getFirst();

        StringBuilder message = new StringBuilder();
        for (String line : ChatUtils.getMessageList("commands.playerinfo.string.header")) {
            line = line.replace("%target%", targetName)
                    .replace("%prefix%", ConfigManager.getPrefix());
            line = ChatUtils.formatColor(line);
            message.append(line).append("\n");
        }

        message.append(ChatUtils.getMessage("commands.playerinfo.string.main_team", null)
                        .replace("%target%", targetName)
                        .replace("%display_name%", mainTeam.getDisplayName())
                        .replace("%team%", mainTeam.getName())
                        .replace("%priority%", String.valueOf(mainTeam.getPriority())))
                .append("\n");

        message.append(ChatUtils.getMessage("commands.playerinfo.string.team_list_header", null).replace("%target%", targetName)).append("\n");

        for (Team team : playerTeams) {
            message.append(ChatUtils.getMessage("commands.playerinfo.string.team_list_row", null)
                            .replace("%target%", targetName)
                            .replace("%display_name%", team.getDisplayName())
                            .replace("%team%", team.getName())
                            .replace("%priority%", String.valueOf(team.getPriority())))
                    .append("\n");
        }

        for (String line : ChatUtils.getMessageList("commands.playerinfo.string.footer")) {
            line = line.replace("%target%", targetName)
                    .replace("%prefix%", ConfigManager.getPrefix());
            line = ChatUtils.formatColor(line);
            message.append(line).append("\n");
        }

        sender.sendMessage(message.toString());
        return true;
    }

    private boolean handleList(CommandSender sender) {
        Set<Team> teams = TeamManager.getAllTeams();
        if (teams.isEmpty()) {
            sender.sendMessage(ChatUtils.getMessage("commands.list.empty", null));
            return false;
        }

        StringBuilder message = new StringBuilder();
        message.append(ChatUtils.getMessage("commands.list.string.header", null)).append("\n");
        for (Team team : teams) {
            message.append(ChatUtils.getMessage("commands.list.string.row", null)
                            .replace("%team%", team.getName())
                            .replace("%priority%", String.valueOf(team.getPriority()))
                            .replace("%display_name%", team.getDisplayName()))
                    .append("\n");
        }
        sender.sendMessage(message.toString());
        return true;
    }
    private boolean handleJoin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player player) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_specified.join", null)
                        .replace("%target%", player.getName()));
            } else {
                sender.sendMessage("You must specify a team.");
            }
            return false;
        }

        String teamName = args[1];
        boolean allTeams = teamName.equals("*");

        Set<Team> teamsSet;
        if (allTeams) {
            teamsSet = TeamManager.getAllTeams();
            if (teamsSet.isEmpty()) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.anyteam_not_found", null));
                return false;
            }
        } else {
            Team team = TeamManager.getTeam(teamName);
            if (team == null) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null)
                        .replace("%team%", teamName));
                return false;
            }
            teamsSet = Collections.singleton(team);
        }

        // Sí especifican jugador
        if (args.length > 2) {
            String targetName = args[2];
            boolean allTargets = targetName.equals("*");

            Set<String> affectedPlayers = resolveTargets(targetName, teamsSet);

            if (affectedPlayers.isEmpty()) {
                String msgKey = allTeams
                        ? "error.commands.join.other.none_team_or_target"
                        : "error.commands.join.other.none_target";

                sender.sendMessage(ChatUtils.getMessage(msgKey, null)
                        .replace("%team%", teamName)
                        .replace("%target%", targetName));
                return false;
            }

            for (String playerName : affectedPlayers) {
                for (Team team : teamsSet) {
                    if (!TeamManager.isInTeam(playerName, team)) {
                        TeamManager.joinTeam(playerName, team);
                    }
                }
            }

            String msgKey;
            if (allTeams && allTargets) {
                msgKey = "commands.join.other.success_all_all";
            } else if (allTeams) {
                msgKey = "commands.join.other.success_target_all";
            } else if (allTargets) {
                msgKey = "commands.join.other.success_all_target";
            } else {
                msgKey = "commands.join.other.success";
            }

            sender.sendMessage(ChatUtils.getMessage(msgKey, null)
                    .replace("%team%", teamName)
                    .replace("%target%", targetName));
            return true;
        }

        // Si no especifica jugador, debe ser jugador ejecutando el comando
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        if (allTeams) {
            TeamManager.joinAllTeams(player.getName());

            player.sendMessage(ChatUtils.getMessage("commands.join.self.success_all", null)
                    .replace("%target%", player.getName()));
            return true;
        }

        Team team = teamsSet.iterator().next();
        if (TeamManager.isInTeam(player.getName(), team)) {
            player.sendMessage(ChatUtils.getMessage("error.commands.join.self.already_in_team", null)
                    .replace("%team%", teamName)
                    .replace("%target%", player.getName()));
            return false;
        }

        TeamManager.joinTeam(player.getName(), team);
        player.sendMessage(ChatUtils.getMessage("commands.join.self.success", null)
                .replace("%team%", teamName)
                .replace("%target%", player.getName()));

        return true;
    }
    private boolean handleLeave(CommandSender sender, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player player) {
                player.sendMessage(ChatUtils.getMessage("error.commands.team_not_specified.leave", null));
            } else {
                sender.sendMessage("You must specify a team.");
            }
            return false;
        }

        String teamName = args[1];
        boolean allTeams = teamName.equals("*");

        Team team = null;
        if (!allTeams) {
            team = TeamManager.getTeam(teamName);
            if (team == null) {
                sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null)
                        .replace("%team%", teamName));
                return false;
            }
        }

        if (allTeams && dontHavePermission(sender, "xteams.command.leave.all")) {
            sender.sendMessage(ChatUtils.getMessage("error.commands.all_not_permission.leave", null));
            return false;
        }

        if (args.length > 2) {
            String targetName = args[2];
            boolean allTargets = targetName.equals("*");
            Set<Team> teamsSet;
            if (allTeams) {
                teamsSet = new HashSet<>(TeamManager.getAllTeams());
            } else {
                Team singleTeam = TeamManager.getTeam(teamName);
                if (singleTeam == null) {
                    sender.sendMessage(ChatUtils.getMessage("error.commands.team_not_found", null)
                            .replace("%team%", teamName));
                    return false;
                }
                teamsSet = Collections.singleton(singleTeam);
            }

            Set<String> affectedPlayers = resolveTargets(targetName, teamsSet);

            if (affectedPlayers.isEmpty()) {
                String msgKey = allTeams
                        ? "error.commands.leave.other.none_in_anyteam"
                        : "error.commands.leave.other.none_in_team";

                sender.sendMessage(ChatUtils.getMessage(msgKey, null)
                        .replace("%team%", teamName)
                        .replace("%target%", targetName));
                return false;
            }

            Set<String> playersCopy = new HashSet<>(affectedPlayers);
            for (String playerName : playersCopy) {
                if (allTeams) {
                    TeamManager.leaveAllTeams(playerName);
                } else {
                    TeamManager.leaveTeam(playerName, team);
                }
            }

            String msgKey;
            if (allTeams && allTargets) {
                msgKey = "commands.leave.other.all.success_all";
            } else if (allTeams) {
                msgKey = "commands.leave.other.target.success_all";
            } else if (allTargets) {
                msgKey = "commands.leave.other.all.success";
            } else {
                msgKey = "commands.leave.other.target.success";
            }

            sender.sendMessage(ChatUtils.getMessage(msgKey, null)
                    .replace("%team%", teamName)
                    .replace("%target%", targetName));

            return true;
        }

        // Si no especifica jugador, debe ser jugador que ejecuta el comando
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        boolean inTeam = allTeams
                ? TeamManager.isInAnyTeam(player.getName())
                : TeamManager.isInTeam(player.getName(), team);

        if (!inTeam) {
            String msgKey = allTeams
                    ? "error.commands.leave.self.not_in_anyteam"
                    : "error.commands.leave.self.not_in_team";

            player.sendMessage(ChatUtils.getMessage(msgKey, null)
                    .replace("%team%", teamName)
                    .replace("%target%", player.getName()));
            return false;
        }

        if (allTeams) {
            TeamManager.leaveAllTeams(player.getName());
            player.sendMessage(ChatUtils.getMessage("commands.leave.self.success_all", null)
                    .replace("%target%", player.getName()));
        } else {
            TeamManager.leaveTeam(player.getName(), team);
            player.sendMessage(ChatUtils.getMessage("commands.leave.self.success", null)
                    .replace("%team%", teamName)
                    .replace("%target%", player.getName()));
        }

        return true;
    }

    private void handleSync(CommandSender sender) {
        int count = 0;
        for (String teamName : TeamManager.listTeams()) {
            Team team = TeamManager.getTeam(teamName);
            for (String memberName : team.getMembers()) {
                Player player = Bukkit.getPlayerExact(memberName);
                if (player != null && player.isOnline()) {
                    if (XTeams.isEnabledLuckPermsHook()) {
                        LuckPermsGroupManager.applyGroup(player, team.getName());
                    }
                    if (XTeams.isEnabledMinecraftTeamHook()) {
                        MinecraftTeamManager.applyGroup(player, team.getName());
                    }
                    count++;
                }
            }
        }

        sender.sendMessage(ChatUtils.getMessage("commands.sync.success", null).replace("%count%", String.valueOf(count)));
    }


    private boolean handleReload(CommandSender sender) {
        plugin.reloadConfig();
        ConfigManager.reloadMessages();
        ConfigManager.loadTeamsFromConfig();
        if (XTeams.isEnabledAutoTeam()) {
            AutoTeamManager.load();
        }
        sender.sendMessage(ChatUtils.getMessage("commands.reload.success", null));
        return false;
    }
    private boolean handleHelp(CommandSender sender) {
        List<String> helpMessages = ConfigManager.getMessageConfig().getStringList("commands.help");

        if (helpMessages.isEmpty()) {
            helpMessages = List.of(
                    " ",
                    " ",
                    "                            #ffbaff&lx&r&lTeams &8» &r&fHelp",
                    " ",
                    "                    #fff18d&lᴘʟᴜɢɪɴ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴛᴇᴀᴍꜱ ʜᴇʟᴘ #707070» #ccccccShows this help message",
                    "&f  /xᴛᴇᴀᴍꜱ ʀᴇʟᴏᴀᴅ #707070» #ccccccReloads the plugin configuration",
                    "&f  /xᴛᴇᴀᴍꜱ ɪɴꜰᴏ #707070- #ccccccDisplays information about the plugin.",
                    " ",
                    "                       #fff18d&lᴛᴇᴀᴍꜱ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴛᴇᴀᴍꜱ ᴄʀᴇᴀᴛᴇ <ᴛᴇᴀᴍ> <ᴘʀɪᴏʀɪᴛʏ> #707070- #ccccccCreate a team.",
                    "&f  /xᴛᴇᴀᴍꜱ ᴅᴇʟᴇᴛᴇ <ᴛᴇᴀᴍ> #707070- #ccccccDelete a team (type * on <team> for delete all the teams).",
                    "&f  /xᴛᴇᴀᴍꜱ ꜱᴇᴛᴅɪꜱᴘʟᴀʏ \"<ᴅɪꜱᴘʟᴀʏ ɴᴀᴍᴇ>\" #707070- #ccccccSets the display name of a team.",
                    "&f  /xᴛᴇᴀᴍꜱ ᴊᴏɪɴ <ᴛᴇᴀᴍ> <ᴘʟᴀʏᴇʀ> #707070- #ccccccJoin a team (leave the player empty to join yourself).",
                    "&f  /xᴛᴇᴀᴍꜱ ʟᴇᴀᴠᴇ <ᴛᴇᴀᴍ / *> <ᴘʟᴀʏᴇʀ / *> #707070- #ccccccLeave a team (leave the player empty to leave yourself).",
                    " ",
                    "                 #fff18d&lɪɴꜰᴏʀᴍᴀᴛɪᴏɴ ᴄᴏᴍᴍᴀɴᴅꜱ",
                    "&f  /xᴛᴇᴀᴍꜱ ʟɪꜱᴛ #707070- #ccccccDisplays the list of teams registered.",
                    "&f  /xᴛᴇᴀᴍꜱ ᴛᴇᴀᴍɪɴꜰᴏ <ᴛᴇᴀᴍ> #707070- #ccccccGet information about a team.",
                    "&f  /xᴛᴇᴀᴍꜱ ᴘʟᴀʏᴇʀɪɴꜰᴏ <ᴘʟᴀʏᴇʀ> #707070- #ccccccGet information about a player.",
                    " ",
                    " "
            );
        }

        for (String l : helpMessages) {
            sender.sendMessage(ChatUtils.formatColor(l));
        }
        return false;
    }
    private boolean handleInfo(CommandSender sender) {
        String placeholderStatus = XTeams.isWorkingPlaceholderAPI() ? "#a0ff72✔" : "#ff7272✖";
        String luckPermsHookStatus = XTeams.isEnabledLuckPermsHook() ? "#a0ff72✔" : "#ff7272✖";
        String minecraftTeamHookStatus = XTeams.isEnabledMinecraftTeamHook() ? "#a0ff72✔" : "#ff7272✖";
        String autoTeamStatus = XTeams.isEnabledAutoTeam() ? "#a0ff72✔" : "#ff7272✖";
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&8                            #ffbaff&lx&r&lTeams &8» &r&fInfo"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                           ᴍᴀᴅᴇ ʙʏ"));
        sender.sendMessage(ChatUtils.formatColor("&f                           xDrygo #707070» &7&o(@eldrygo)"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                  ʀᴜɴɴɪɴɢ ᴘʟᴜɢɪɴ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f                                    " + plugin.version));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ꜰᴇᴀᴛᴜʀᴇꜱ ᴇɴᴀʙʟᴇᴅ"));
        sender.sendMessage(ChatUtils.formatColor("&f                            ᴘʟᴀᴄᴇʜᴏʟᴅᴇʀᴀᴘɪ #707070» #FFFAAB" + placeholderStatus));
        sender.sendMessage(ChatUtils.formatColor("&f                                ʟᴜᴄᴋᴘᴇʀᴍꜱ #707070» #FFFAAB" + luckPermsHookStatus));
        sender.sendMessage(ChatUtils.formatColor("&f                            ᴍɪɴᴇᴄʀᴀꜰᴛ ᴛᴇᴀᴍꜱ #707070» #FFFAAB" + minecraftTeamHookStatus));
        sender.sendMessage(ChatUtils.formatColor("&f                                ᴀᴜᴛᴏ-ᴛᴇᴀᴍ #707070» #FFFAAB" + autoTeamStatus));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l                      ᴠᴇʀꜱɪᴏɴ ᴄʜᴀɴɢᴇꜱ"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070701. #FFFAABInternal performance improvements"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070702. #FFFAABFixed duplicate saves when"));
        sender.sendMessage(ChatUtils.formatColor("&f            #707070   #FFFAABjoining, leaving or deleting teams"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070703. #FFFAABOptimized team data system"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070704. #FFFAABImproved data handling"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070705. #FFFAABImproved placeholder support"));
        sender.sendMessage(ChatUtils.formatColor("&f            #7070706. #FFFAABIdentification by UUID or username"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("#fff18d&l               ᴅʀʏɢᴏ'ꜱ ɴᴏᴛᴇ ᴏꜰ ᴛʜᴇ ᴠᴇʀꜱɪᴏɴ"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB         Cleaned up a lot of things under the hood,"));
        sender.sendMessage(ChatUtils.formatColor("&f  #FFFAAB         the plugin should feel smoother now. Enjoy!"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        sender.sendMessage(ChatUtils.formatColor("&7"));
        return false;
    }

    private boolean dontHavePermission(CommandSender sender, String perm) {
        return !sender.hasPermission(perm) && !sender.hasPermission("xteams.admin") && !sender.isOp();
    }

    private Set<String> resolveTargets(String targetArg, Set<Team> teamsScope) {
        if (!PlayerSelector.isSelector(targetArg)) {
            Set<String> single = new HashSet<>();
            single.add(targetArg);
            return single;
        }
        return PlayerSelector.resolve(targetArg);
    }
}