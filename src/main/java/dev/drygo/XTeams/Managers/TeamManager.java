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

    // -------------------------------------------------------------------------
    // Internos: operan directamente sobre el mapa de equipos
    // -------------------------------------------------------------------------

    public static void addTeam(Team team) {
        teams.put(team.getName(), team);
    }

    public static void loadTeam(Team team) {
        teams.put(team.getName(), team);
    }

    // -------------------------------------------------------------------------
    // Consulta de equipos
    // -------------------------------------------------------------------------

    public static Team getTeam(String teamName) {
        return teams.get(teamName);
    }

    public static Set<Team> getAllTeams() {
        return new HashSet<>(teams.values());
    }

    public static boolean teamExists(String teamName) {
        return teams.containsKey(teamName);
    }

    public static List<String> listTeams() {
        return new ArrayList<>(teams.keySet());
    }

    public static Map<String, Object> getTeamInfo(Team team) {
        Map<String, Object> info = new HashMap<>();
        if (team != null) {
            info.put("name", team.getName());
            info.put("displayName", team.getDisplayName());
            // Devolvemos los nombres visibles para mostrar, nunca los IDs internos
            info.put("members", team.getResolvedMemberNames());
            info.put("priority", team.getPriority());
        }
        return info;
    }

    // -------------------------------------------------------------------------
    // Creación / eliminación de equipos
    // -------------------------------------------------------------------------

    public static void createTeam(String name, String displayName, int priority, Set<String> members) {
        if (!teamExists(name)) {
            Team team = new Team(name, displayName, priority, members);
            addTeam(team);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(new TeamCreateEvent(team));
        }
    }

    public static void deleteTeam(Team team) {
        if (team != null) {
            teams.remove(team.getName());
            Bukkit.getPluginManager().callEvent(new TeamDeleteEvent(team.getName()));
            ConfigManager.saveTeamsToConfig();
        }
    }

    public static void deleteAllTeams() {
        for (Team team : getAllTeams()) {
            teams.remove(team.getName());
            Bukkit.getPluginManager().callEvent(new TeamDeleteEvent(team.getName()));
        }
        ConfigManager.saveTeamsToConfig();
    }

    // -------------------------------------------------------------------------
    // Gestión de miembros — todas las entradas son NICKNAMES visibles;
    // la conversión a identificador interno ocurre aquí.
    // -------------------------------------------------------------------------

    /**
     * Hace que un jugador (por nickname) se una a un equipo.
     * Internamente guarda UUID o nickname según el modo configurado.
     */
    public static void joinTeam(String nickname, Team team) {
        String id = PlayerIdentifier.fromName(nickname);
        if (team != null && !team.hasMember(id)) {
            team.addMember(id);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(
                    new TeamJoinEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
        }
    }

    /**
     * Hace que un jugador (por nickname) abandone un equipo.
     */
    public static void leaveTeam(String nickname, Team team) {
        String id = PlayerIdentifier.fromName(nickname);
        if (team != null && team.hasMember(id)) {
            team.removeMember(id);
            ConfigManager.saveTeamsToConfig();
            Bukkit.getPluginManager().callEvent(
                    new TeamLeaveEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
        }
    }

    /**
     * Hace que un jugador (por nickname) se una a todos los equipos existentes.
     */
    public static void joinAllTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        for (Team team : teams.values()) {
            if (!team.hasMember(id)) {
                team.addMember(id);
                Bukkit.getPluginManager().callEvent(
                        new TeamJoinEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
            }
        }
        ConfigManager.saveTeamsToConfig();
    }

    /**
     * Hace que un jugador (por nickname) abandone todos los equipos.
     */
    public static void leaveAllTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        for (Team team : teams.values()) {
            if (team.hasMember(id)) {
                team.removeMember(id);
                Bukkit.getPluginManager().callEvent(
                        new TeamLeaveEvent(Bukkit.getOfflinePlayer(nickname), team.getName()));
            }
        }
        ConfigManager.saveTeamsToConfig();
    }

    // -------------------------------------------------------------------------
    // Consultas de pertenencia — todas las entradas son NICKNAMES visibles
    // -------------------------------------------------------------------------

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

    /**
     * Devuelve todos los equipos a los que pertenece un jugador (por nickname).
     */
    public static List<Team> getPlayerTeams(String nickname) {
        String id = PlayerIdentifier.fromName(nickname);
        List<Team> result = new ArrayList<>();
        for (Team team : teams.values()) {
            if (team.hasMember(id)) result.add(team);
        }
        return result;
    }

    /**
     * Devuelve el equipo de mayor prioridad al que pertenece un jugador (por nickname).
     */
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

    // -------------------------------------------------------------------------
    // Utilidades de miembros
    // -------------------------------------------------------------------------

    public static Set<String> getTeamMembers(Team team) {
        return team != null ? team.getMembers() : new HashSet<>();
    }

    /**
     * Devuelve todos los identificadores internos de jugadores en algún equipo.
     * Para obtener los nicknames visibles usa {@link #getAllPlayerNamesInTeams()}.
     */
    public static Set<String> getAllPlayersInTeams() {
        Set<String> all = new HashSet<>();
        for (Team team : getAllTeams()) {
            all.addAll(getTeamMembers(team));
        }
        return all;
    }

    /**
     * Devuelve los nicknames visibles de todos los jugadores en algún equipo.
     * Siempre resuelve UUIDs a nombres cuando el modo es UUID.
     */
    public static Set<String> getAllPlayerNamesInTeams() {
        Set<String> names = new HashSet<>();
        for (String id : getAllPlayersInTeams()) {
            String name = PlayerIdentifier.resolveName(id);
            if (name != null) names.add(name);
        }
        return names;
    }

    public static void setDisplayName(Team team, String displayName) {
        team.setDisplayName(displayName);
        ConfigManager.saveTeamsToConfig();
    }
}