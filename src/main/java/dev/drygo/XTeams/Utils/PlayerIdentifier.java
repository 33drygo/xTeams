package dev.drygo.XTeams.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerIdentifier {

    public enum Mode { UUID, NICKNAME }

    private static Mode mode = Mode.NICKNAME;

    public static void setMode(Mode m) {
        mode = m;
    }

    public static Mode getMode() {
        return mode;
    }

    /** Obtiene el identificador a usar dado un nombre de jugador */
    public static String fromName(String name) {
        if (mode == Mode.UUID) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            return op.getUniqueId().toString();
        }
        return name;
    }

    /** Obtiene el identificador a usar dado un Player online */
    public static String fromPlayer(Player player) {
        if (mode == Mode.UUID) {
            return player.getUniqueId().toString();
        }
        return player.getName();
    }

    /** Obtiene el identificador a usar dado un OfflinePlayer */
    public static String fromOfflinePlayer(OfflinePlayer player) {
        if (mode == Mode.UUID) {
            return player.getUniqueId().toString();
        }
        String name = player.getName();
        return name != null ? name : player.getUniqueId().toString();
    }

    /** Resuelve el nombre visible dado un identificador almacenado */
    public static String resolveName(String identifier) {
        if (mode == Mode.UUID) {
            try {
                OfflinePlayer op = Bukkit.getOfflinePlayer(java.util.UUID.fromString(identifier));
                String name = op.getName();
                return name != null ? name : identifier;
            } catch (IllegalArgumentException e) {
                return identifier; // fallback si no es UUID válido
            }
        }
        return identifier;
    }
}