package dev.drygo.XTeams.Managers;

import dev.drygo.XTeams.Utils.PlayerIdentifier;
import org.bukkit.Bukkit;
import dev.drygo.XTeams.API.Events.TeamCreateEvent;
import dev.drygo.XTeams.API.Events.TeamDeleteEvent;
import dev.drygo.XTeams.API.Events.TeamJoinEvent;
import dev.drygo.XTeams.API.Events.TeamLeaveEvent;
import dev.drygo.XTeams.Models.Team;

import java.util.*;

public class TeamManager {
    private static final Map<String, Team> teams = new HashMap<>();

    public static void addTeam(Team team) {
        teams.put(team.getName(), team);
        
    }

    public static Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    public static void deleteTeam(Team team) {
        if (team != null) {
            teams.remove(team.getName());
            Bukkit.getPluginManager().callEvent(new TeamDeleteEvent(team.getName()));
            ConfigManager.saveTeamsToConfig();
        }
    }

    public static Set<Team> getAllTeams() {
        return new HashSet<>(teams.values());
    }

    public static boolean teamExists(String teamName) {
        return teams.containsKey(teamName);
    }

    public static Team getPlayerTeam(String nickname) {
        List<Team> playerTeams = getPlayerTeams(nickname);
        Team highest = null;
        for (Team team : playerTeams) {
            if (highest == null || team.getPriority() > highest.getPriority()) {
                highest = team;
            }
        }
        return highest;
    }

    public static Set<String> getTeamMembers(Team team) {
        return team != null ? team.getMembers() : new HashSet<>();
    }

    public static Set<String> getAllPlayersInTeams() {
        Set<String> allPlayers = new HashSet<>();
        for (Team team : getAllTeams()) {
            allPlayers.addAll(getTeamMembers(team));
        }
        return allPlayers;
    }

    public static void createTeam(String name, String displayName, int priority, Set<String> members) {
        if (!teamExists(name)) {
            Team team = new Team(name, displayName, priority, members);
            addTeam(team);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(new TeamCreateEvent(team));
        }
    }

    public static void deleteAllTeams() {
        for (Team team : getAllTeams()) {
            teams.remove(team.getName());
            Bukkit.getPluginManager().callEvent(new TeamDeleteEvent(team.getName()));
        }
        ConfigManager.saveTeamsToConfig();
    }

    public static List<String> listTeams() {
        List<String> teamNames = new ArrayList<>();
        for (Team team : teams.values()) {
            teamNames.add(team.getName());
        }
        return teamNames;
    }

    public static Map<String, Object> getTeamInfo(Team team) {
        Map<String, Object> teamInfo = new HashMap<>();
        if (team != null) {
            teamInfo.put("name", team.getName());
            teamInfo.put("displayName", team.getDisplayName());
            teamInfo.put("members", team.getMembers());
            teamInfo.put("priority", team.getPriority());
        }
        return teamInfo;
    }

    public static void joinTeam(String nickname, Team team) {
        String id = PlayerIdentifier.fromName(nickname);
        if (team != null && !team.hasMember(id)) {
            team.addMember(id);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(new TeamJoinEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
        }
    }

    public static void leaveTeam(String nickname, Team team) {
        String id = PlayerIdentifier.fromName(nickname);
        if (team != null && team.hasMember(id)) {
            team.removeMember(id);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(new TeamLeaveEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
        }
    }

    public static void joinAllTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        for (Team team : teams.values()) {
            if (!team.hasMember(id)) {
                team.addMember(id);
                Bukkit.getPluginManager().callEvent(new TeamJoinEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
            }
        }
        ConfigManager.saveTeamsToConfig();
    }

    public static void leaveAllTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        for (Team team : teams.values()) {
            if (team.hasMember(id)) {
                team.removeMember(id);
                Bukkit.getPluginManager().callEvent(new TeamLeaveEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
            }
        }
        ConfigManager.saveTeamsToConfig();
    }

    public static void setDisplayName(Team team, String displayName) {
        team.setDisplayName(displayName);
        ConfigManager.saveTeamsToConfig();
    }

    public static boolean isInTeam(String nickname, Team team) {
        String id = PlayerIdentifier.fromName(nickname);
        return team != null && team.hasMember(id);
    }

    public static boolean isInAnyTeam(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        for (Team team : teams.values()) {
            if (team.hasMember(id)) return true;
        }
        return false;
    }

    public static List<Team> getPlayerTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        List<Team> result = new ArrayList<>();
        for (Team team : teams.values()) {
            if (team.hasMember(id)) result.add(team);
        }
        return result;
    }


    public static void loadTeam(Team team) {
        teams.put(team.getName(), team);
    }
}
