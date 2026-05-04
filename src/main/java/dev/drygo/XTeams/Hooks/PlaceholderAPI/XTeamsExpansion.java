package dev.drygo.XTeams.Hooks.PlaceholderAPI;

import dev.drygo.XTeams.Managers.TeamManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import dev.drygo.XTeams.XTeams;
import dev.drygo.XTeams.Models.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Placeholders disponibles:
 *
 * --- Del jugador que ejecuta el placeholder ---
 * %xteams_team%                        → nombre del equipo principal del jugador
 * %xteams_team_display%                → displayName del equipo principal
 * %xteams_team_priority%               → prioridad del equipo principal
 * %xteams_in_team_<team>%              → true/false si el jugador está en <team>
 * %xteams_in_any_team%                 → true/false si el jugador está en algún equipo
 * %xteams_team_count%                  → cantidad de equipos en los que está el jugador
 * %xteams_teams_list%                  → lista de equipos del jugador separados por ", "
 *
 * --- De otro jugador (por nombre) ---
 * %xteams_player_team_<player>%        → equipo principal de <player>
 * %xteams_player_team_display_<player>% → displayName del equipo principal de <player>
 * %xteams_player_in_team_<player>:<team>% → true/false si <player> está en <team>
 * %xteams_player_in_any_team_<player>% → true/false si <player> está en algún equipo
 * %xteams_player_team_count_<player>%  → cantidad de equipos de <player>
 *
 * --- De un equipo específico ---
 * %xteams_exists_<team>%               → true/false si el equipo existe
 * %xteams_display_<team>%              → displayName del equipo
 * %xteams_priority_<team>%             → prioridad del equipo
 * %xteams_size_<team>%                 → número de miembros del equipo
 * %xteams_members_<team>%              → miembros del equipo separados por ", "
 *
 * --- Globales ---
 * %xteams_total_teams%                 → número total de equipos registrados
 * %xteams_total_players%               → número total de jugadores en algún equipo
 * %xteams_all_teams%                   → lista de todos los equipos separados por ", "
 */
public class XTeamsExpansion extends PlaceholderExpansion {

    private final XTeams plugin;

    public XTeamsExpansion(XTeams plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "xteams"; }

    @Override
    public @NotNull String getAuthor() { return "Drygo"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        // ── Globales ──────────────────────────────────────────────────────────

        if (params.equals("total_teams")) {
            return String.valueOf(TeamManager.getAllTeams().size());
        }

        if (params.equals("total_players")) {
            return String.valueOf(TeamManager.getAllPlayersInTeams().size());
        }

        if (params.equals("all_teams")) {
            return String.join(", ", TeamManager.listTeams());
        }

        // ── Por equipo ────────────────────────────────────────────────────────

        if (params.startsWith("exists_")) {
            String teamName = params.substring("exists_".length());
            return bool(TeamManager.teamExists(teamName));
        }

        if (params.startsWith("display_")) {
            String teamName = params.substring("display_".length());
            Team team = TeamManager.getTeam(teamName);
            return team != null ? team.getDisplayName() : "null";
        }

        if (params.startsWith("priority_")) {
            String teamName = params.substring("priority_".length());
            Team team = TeamManager.getTeam(teamName);
            return team != null ? String.valueOf(team.getPriority()) : "null";
        }

        if (params.startsWith("size_")) {
            String teamName = params.substring("size_".length());
            Team team = TeamManager.getTeam(teamName);
            return team != null ? String.valueOf(team.getMembers().size()) : "null";
        }

        if (params.startsWith("members_")) {
            String teamName = params.substring("members_".length());
            Team team = TeamManager.getTeam(teamName);
            return team != null ? String.join(", ", team.getResolvedMemberNames()) : "null";
        }

        // ── Por otro jugador ──────────────────────────────────────────────────

        if (params.startsWith("player_team_display_")) {
            String targetName = params.substring("player_team_display_".length());
            return getMainTeamField(targetName, false);
        }

        if (params.startsWith("player_team_count_")) {
            String targetName = params.substring("player_team_count_".length());
            return String.valueOf(TeamManager.getPlayerTeams(targetName).size());
        }

        if (params.startsWith("player_in_any_team_")) {
            String targetName = params.substring("player_in_any_team_".length());
            return bool(TeamManager.isInAnyTeam(targetName));
        }

        if (params.startsWith("player_in_team_")) {
            // formato: player_in_team_<player>:<team>
            String rest = params.substring("player_in_team_".length());
            String[] parts = rest.split(":", 2);
            if (parts.length == 2) {
                Team team = TeamManager.getTeam(parts[1]);
                return bool(team != null && TeamManager.isInTeam(parts[0], team));
            }
            return "null";
        }

        if (params.startsWith("player_team_")) {
            // va después de player_team_display_ para no colisionar
            String targetName = params.substring("player_team_".length());
            return getMainTeamField(targetName, true);
        }

        // ── Del jugador actual ────────────────────────────────────────────────

        if (player == null) return "null";
        String playerName = player.getName();
        if (playerName == null) return "null";

        if (params.equals("team")) {
            return getMainTeamField(playerName, true);
        }

        if (params.equals("team_display")) {
            return getMainTeamField(playerName, false);
        }

        if (params.equals("team_priority")) {
            Team main = TeamManager.getPlayerTeam(playerName);
            return main != null ? String.valueOf(main.getPriority()) : "null";
        }

        if (params.equals("in_any_team")) {
            return bool(TeamManager.isInAnyTeam(playerName));
        }

        if (params.equals("team_count")) {
            return String.valueOf(TeamManager.getPlayerTeams(playerName).size());
        }

        if (params.equals("teams_list")) {
            List<Team> playerTeams = TeamManager.getPlayerTeams(playerName);
            if (playerTeams.isEmpty()) return "none";
            StringBuilder sb = new StringBuilder();
            playerTeams.stream()
                    .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                    .forEach(t -> sb.append(t.getName()).append(", "));
            return sb.substring(0, sb.length() - 2);
        }

        if (params.startsWith("in_team_")) {
            String teamName = params.substring("in_team_".length());
            Team team = TeamManager.getTeam(teamName);
            return bool(team != null && TeamManager.isInTeam(playerName, team));
        }

        return null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getMainTeamField(String playerName, boolean returnName) {
        Team main = TeamManager.getPlayerTeam(playerName);
        if (main == null) return "null";
        return returnName ? main.getName() : main.getDisplayName();
    }

    private String bool(boolean value) {
        return value ? "true" : "false";
    }
}