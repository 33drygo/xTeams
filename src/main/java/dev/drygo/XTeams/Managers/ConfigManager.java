package dev.drygo.XTeams.Managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.drygo.XTeams.Models.Team;
import dev.drygo.XTeams.XTeams;
import dev.drygo.XTeams.Utils.ChatUtils;

import java.io.File;
import java.util.*;

public class ConfigManager {
    private static XTeams plugin;
    private static FileConfiguration messagesConfig;

    public static void init(XTeams plugin) {
        ConfigManager.plugin = plugin;
    }

    public static void loadConfig() {
        try {
            plugin.saveDefaultConfig();
            plugin.reloadConfig();
            plugin.getLogger().info("✅ The config.yml file successfully loaded.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ Failed on loading config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void reloadMessages() {
        try {
            File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
            if (!messagesFile.exists()) {
                plugin.saveResource("messages.yml", false);
                plugin.getLogger().info("✅ The messages.yml file did not exist, it has been created.");
            } else {
                plugin.getLogger().info("✅ The messages.yml file has been loaded successfully.");
            }

            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        } catch (Exception e) {
            plugin.getLogger().severe("❌ Failed to load messages configuration due to an unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadTeamsFromConfig() {
        FileConfiguration config = plugin.getConfig();

        if (config.contains("teams")) {
            for (String teamName : Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false)) {
                List<String> players = config.getStringList("teams." + teamName + ".members");
                String displayName = config.getString("teams." + teamName + ".displayName", teamName);
                int priority = config.getInt("teams." + teamName + ".priority", 0);

                Set<String> playerSet = new HashSet<>(players);
                Team team = new Team(teamName, displayName, priority, playerSet);
                TeamManager.loadTeam(team);

                plugin.getLogger().info("Team loaded: " + teamName);
            }
        }
    }

    public static void saveTeamsToConfig() {
        FileConfiguration config = plugin.getConfig();
        if (!XTeams.isTeamsLoaded()) {
            plugin.getLogger().warning("Skipping saveTeamsToConfig(): teams not loaded yet");
            return;
        }
        ConfigurationSection section = config.getConfigurationSection("teams");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                section.set(key, null);
            }
        }
        for (Team team : TeamManager.getAllTeams()) {
            if (team == null) {
                plugin.getLogger().warning("A null team was encountered, skipping save.");
                continue;
            }
            String teamName = team.getName();
            Set<String> members = team.getMembers();

            List<String> memberList = new ArrayList<>(members);

            config.set("teams." + teamName + ".members", memberList);
            config.set("teams." + teamName + ".displayName", team.getDisplayName());
            config.set("teams." + teamName + ".priority", team.getPriority());
        }
        plugin.saveConfig();
    }

    public static String getPrefix() { return plugin.prefix; }
    public static void setPrefix(String newPrefix) { plugin.prefix = newPrefix; }
    public static FileConfiguration getMessageConfig() {
        return messagesConfig;
    }
}
