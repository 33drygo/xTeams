package dev.drygo.XTeams;

import dev.drygo.XTeams.Utils.LoadUtils;
import org.bukkit.plugin.java.JavaPlugin;
import dev.drygo.XTeams.Managers.ConfigManager;
import dev.drygo.XTeams.Utils.ChatUtils;
import dev.drygo.XTeams.Utils.LogsUtils;

public class XTeams extends JavaPlugin {
    public String version;
    public String prefix;

    private static boolean workingPlaceholderAPI = false;
    private static boolean enabledLuckPermsHook = false;
    private static boolean enabledMinecraftTeamHook = false;
    private static boolean enabledAutoTeam = false;

    private static boolean teamsLoaded = false;

    @Override
    public void onEnable() {
        version = getDescription().getVersion();
        ConfigManager.init(this);
        ChatUtils.init(this);
        LogsUtils.init(this);
        LoadUtils.init(this);
        LoadUtils.loadFeatures();
        LogsUtils.sendStartupMessage();
        LogsUtils.sendUpdateMessage();
    }

    @Override
    public void onDisable() {
        LogsUtils.sendShutdownMessage();
    }


    public static boolean isWorkingPlaceholderAPI() {
        return workingPlaceholderAPI;
    }
    public static void setWorkingPlaceholderAPI(boolean workingPlaceholderAPI) {
        XTeams.workingPlaceholderAPI = workingPlaceholderAPI;
    }
    public static boolean isEnabledLuckPermsHook() {
        return enabledLuckPermsHook;
    }
    public static void setEnabledLuckPermsHook(boolean enabledLuckPermsHook) {
        XTeams.enabledLuckPermsHook = enabledLuckPermsHook;
    }
    public static boolean isEnabledMinecraftTeamHook() {
        return enabledMinecraftTeamHook;
    }
    public static void setEnabledMinecraftTeamHook(boolean enabledMinecraftTeamHook) {
        XTeams.enabledMinecraftTeamHook = enabledMinecraftTeamHook;
    }
    public static boolean isEnabledAutoTeam() {
        return enabledAutoTeam;
    }
    public static void setEnabledAutoTeam(boolean enabledAutoTeam) {
        XTeams.enabledAutoTeam = enabledAutoTeam;
    }

    public static boolean isTeamsLoaded() {
        return teamsLoaded;
    }
    public static void setTeamsLoaded(boolean state) {
        teamsLoaded = state;
    }
}