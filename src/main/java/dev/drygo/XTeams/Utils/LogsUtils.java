package dev.drygo.XTeams.Utils;

import dev.drygo.XTeams.UpdateChecker.ModrinthUpdateChecker;
import org.bukkit.Bukkit;
import dev.drygo.XTeams.XTeams;

public class LogsUtils {
    private static XTeams plugin;

    public static void init(XTeams plugin) {
        LogsUtils.plugin = plugin;
    }
    public static void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&d&lx&r&lTeams #a0ff72plugin enabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dVersion: #ffffff" + plugin.version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dDeveloped by: #ffffff" + String.join(", ", plugin.getDescription().getAuthors())));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }
    public static void sendShutdownMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&d&lx&r&lTeams #ff7272plugin disabled!"));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dVersion: #ffffff" + plugin.version));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("#fff18dDeveloped by: #ffffff" + String.join(", ", plugin.getDescription().getAuthors())));
        Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
    }

    public static void sendUpdateMessage() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String latestVersion = ModrinthUpdateChecker.isUpdateAvailable(plugin.version);
            if (latestVersion.equals("false")) return;

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&d&lx&r&lTeams &eNew Update Available!"));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&cCurrent Version: &f" + plugin.version));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&aLatest Version: &f" + latestVersion));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor("&e&lYou can download it at: &fhttps://modrinth.com/plugin/xteams"));
                Bukkit.getConsoleSender().sendMessage(ChatUtils.formatColor(" "));
            });
        });
    }
}
