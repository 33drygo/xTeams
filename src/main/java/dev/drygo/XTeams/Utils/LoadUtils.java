package dev.drygo.XTeams.Utils;

import dev.drygo.XTeams.Commands.XTeamsCommand;
import dev.drygo.XTeams.Commands.XTeamsTabCompleter;
import dev.drygo.XTeams.Hooks.AutoTeam.Managers.AutoTeamManager;
import dev.drygo.XTeams.Hooks.Listeners.TeamListener;
import dev.drygo.XTeams.Hooks.LuckPerms.Managers.LuckPermsGroupManager;
import dev.drygo.XTeams.Hooks.Minecraft.Managers.MinecraftTeamManager;
import dev.drygo.XTeams.Hooks.PlaceholderAPI.XTeamsExpansion;
import dev.drygo.XTeams.Managers.ConfigManager;
import dev.drygo.XTeams.XTeams;
import org.bukkit.Bukkit;

import java.util.Objects;

public class LoadUtils {
    private static XTeams plugin;

    public static void init(XTeams plugin) {
        LoadUtils.plugin = plugin;
    }

    public static void loadFeatures() {
        loadFiles();
        loadCommand();
        loadListeners();
        loadPlaceholderAPI();
        loadHooks();
    }

    public static void loadFiles() {
        ConfigManager.loadConfig();
        ConfigManager.reloadMessages();
        loadIdentifierMode();
        ConfigManager.loadTeamsFromConfig();
        ConfigManager.setPrefix(ChatUtils.formatColor(ConfigManager.getMessageConfig().getString("prefix")));
        XTeams.setTeamsLoaded(true);
    }

    private static void loadListeners() {
        plugin.getServer().getPluginManager().registerEvents(new TeamListener(), plugin);
    }

    private static void loadCommand() {
        if (plugin.getCommand("xteams") == null) {
            plugin.getLogger().severe("❌ Error: xTeams command is not registered in plugin.yml");
        } else {
            Objects.requireNonNull(plugin.getCommand("xteams")).setExecutor(new XTeamsCommand(plugin));
            Objects.requireNonNull(plugin.getCommand("xteams")).setTabCompleter(new XTeamsTabCompleter());
        }
    }
    private static void loadPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new XTeamsExpansion(plugin).register();
            plugin.getLogger().info("✅ PlaceholderAPI detected. Placeholders will work.");
            XTeams.setWorkingPlaceholderAPI(true);
        } else {
            plugin.getLogger().warning("⚠ PlaceholderAPI not detected. Placeholders will not work.");
        }
    }
    private static void loadHooks() {
        if (plugin.getConfig().getBoolean("hooks.luckperms.enabled", false)) {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
                plugin.getLogger().info("✅ LuckPerms detected. LuckPerms sync hook successfully enabled.");
                LuckPermsGroupManager.init(plugin);
                XTeams.setEnabledLuckPermsHook(true);
            } else {
                plugin.getLogger().warning("⚠ LuckPerms not detected. Can't load LuckPerms sync hook.");
            }
        }
        if (plugin.getConfig().getBoolean("hooks.minecraft_team.enabled", false)) {
            plugin.getLogger().info("✅ Minecraft Team sync hook successfully enabled.");
            MinecraftTeamManager.init(plugin);
            XTeams.setEnabledMinecraftTeamHook(true);
        }
        if (plugin.getConfig().getBoolean("hooks.auto_team.enabled", false)) {
            plugin.getLogger().info("✅ Auto Team hook successfully enabled.");
            AutoTeamManager.init(plugin);
            AutoTeamManager.load();
            XTeams.setEnabledAutoTeam(true);
        }
    }

    private static void loadIdentifierMode() {
        String raw = plugin.getConfig().getString("settings.identifier_mode", "nickname");
        String mode = (raw != null ? raw : "nickname").toLowerCase();
        if (mode.equals("uuid")) {
            PlayerIdentifier.setMode(PlayerIdentifier.Mode.UUID);
            plugin.getLogger().info("✅ Identifier mode: UUID (premium)");
        } else {
            PlayerIdentifier.setMode(PlayerIdentifier.Mode.NICKNAME);
            plugin.getLogger().info("✅ Identifier mode: NICKNAME (non-premium)");
        }
    }
}
