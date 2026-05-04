package dev.drygo.XTeams.Hooks.Listeners;

import dev.drygo.XTeams.API.Events.TeamJoinEvent;
import dev.drygo.XTeams.API.Events.TeamLeaveEvent;
import dev.drygo.XTeams.Hooks.AutoTeam.Managers.AutoTeamManager;
import dev.drygo.XTeams.Hooks.LuckPerms.Managers.LuckPermsGroupManager;
import dev.drygo.XTeams.Hooks.Minecraft.Managers.MinecraftTeamManager;
import dev.drygo.XTeams.Managers.TeamManager;
import dev.drygo.XTeams.Models.Team;
import dev.drygo.XTeams.XTeams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeamListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!XTeams.isEnabledAutoTeam()) return;
        if (AutoTeamManager.enabledOpByPass() && player.isOp()) return;
        if (!TeamManager.getPlayerTeams(player.getName()).isEmpty()) return;
        Team autoTeamTeam = TeamManager.getTeam(AutoTeamManager.getAutoTeamTeam());
        if (autoTeamTeam == null) return;
        TeamManager.joinTeam(player.getName(), autoTeamTeam);
    }

    @EventHandler
    public void onTeamJoin(TeamJoinEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        Team team = TeamManager.getTeam(event.getTeamName());
        if (team == null) return;
        if (XTeams.isEnabledLuckPermsHook()) {
            LuckPermsGroupManager.applyGroup(player, team.getName());
        }
        if (XTeams.isEnabledMinecraftTeamHook()) {
            MinecraftTeamManager.applyGroup(player, team.getName());
        }
    }

    @EventHandler
    public void onTeamLeave(TeamLeaveEvent event) {
        Player player = Bukkit.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        Team team = TeamManager.getTeam(event.getTeamName());
        if (team == null) return;
        if (XTeams.isEnabledLuckPermsHook()) {
            LuckPermsGroupManager.removeGroup(player, team.getName());
        }
        if (XTeams.isEnabledMinecraftTeamHook()) {
            MinecraftTeamManager.removeGroup(player, team.getName());
        }
    }
}
