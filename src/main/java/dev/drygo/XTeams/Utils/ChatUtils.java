package dev.drygo.XTeams.Utils;

import dev.drygo.XTeams.Managers.ConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import dev.drygo.XTeams.XTeams;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    private static XTeams plugin;

    public static void init(XTeams plugin) {
        ChatUtils.plugin = plugin;
    }

    public static String formatColor(String message) {
        message = replaceHexColors(message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static String replaceHexColors(String message) {
        Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder color = new StringBuilder("&x");
            for (char c : hexColor.toCharArray()) {
                color.append("&").append(c);
            }
            matcher.appendReplacement(buffer, color.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    public static String getMessage(String path, OfflinePlayer player) {
        String message = ConfigManager.getMessageConfig().isList(path)
                ? String.join("\n", ConfigManager.getMessageConfig().getStringList(path))
                : ConfigManager.getMessageConfig().getString(path);

        if (message == null || message.isEmpty()) {
            plugin.getLogger().warning("[WARNING] Message not found: " + path);
            return ChatUtils.formatColor("&r" + ConfigManager.getPrefix() + " #FF0000&l[ERROR] #FF3535Message not found: " + path);
        }

        if (player != null) {
            message = message.replace("%player%", Objects.requireNonNull(player.getName()));
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }
        } else {
            message = message.replace("%player%", "Unknown");
        }


        message = message.replace("%prefix%", ConfigManager.getPrefix());

        return ChatUtils.formatColor(message);
    }
    public static List<String> getMessageList(String path) {
        return ConfigManager.getMessageConfig().getStringList(path);
    }

}