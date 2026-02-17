package dev.drygo.XTeams.API;

import dev.drygo.XTeams.Managers.TeamManager;
import dev.drygo.XTeams.Models.Team;

import java.util.*;

public class XTeamsAPI {

    public static Team getTeam(String name) {
        return TeamManager.getTeam(name);
    }

    public static Set<Team> getAllTeams() {
        return TeamManager.getAllTeams();
    }

    public static void createTeam(String name, String displayName, int priority, Set<String> members) {
        TeamManager.createTeam(name, displayName, priority, members);
    }

    public static void deleteTeam(Team team) {
        TeamManager.deleteTeam(team);
    }

    public static void setDisplayName(Team team, String displayName) {
        TeamManager.setDisplayName(team, displayName);
    }

    public static Map<String, Object> getTeamInfo(Team team) {
        return TeamManager.getTeamInfo(team);
    }

    public static void joinTeam(String player, Team team) {
        TeamManager.joinTeam(player, team);
    }

    public static void leaveTeam(String player, Team team) {
        TeamManager.leaveTeam(player, team);
    }

    public static void joinAllTeams(String player) {
        TeamManager.joinAllTeams(player);
    }

    public static void leaveAllTeams(String player) {
        TeamManager.leaveAllTeams(player);
    }

    public static boolean isInTeam(String player, Team team) {
        return TeamManager.isInTeam(player, team);
    }

    public static boolean isInAnyTeam(String player) {
        return TeamManager.isInAnyTeam(player);
    }

    public static List<Team> getPlayerTeams(String player) {
        return TeamManager.getPlayerTeams(player);
    }

    public static Team getPlayerTeam(String player) {
        return TeamManager.getPlayerTeam(player);
    }

    public static List<String> listTeamNames() {
        return TeamManager.listTeams();
    }
}
