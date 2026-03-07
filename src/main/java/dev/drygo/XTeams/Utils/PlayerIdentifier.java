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

    /** Obtiene el identificador interno a usar dado un nombre de jugador */
    public static String fromName(String name) {
        if (mode == Mode.UUID) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            return op.getUniqueId().toString();
        }
        return name;
    }

    /** Obtiene el identificador interno a usar dado un Player online */
    public static String fromPlayer(Player player) {
        if (mode == Mode.UUID) {
            return player.getUniqueId().toString();
        }
        return player.getName();
    }

    /** Obtiene el identificador interno a usar dado un OfflinePlayer */
    public static String fromOfflinePlayer(OfflinePlayer player) {
        if (mode == Mode.UUID) {
            return player.getUniqueId().toString();
        }
        String name = player.getName();
        return name != null ? name : player.getUniqueId().toString();
    }

    /**
     * Resuelve el nombre visible (nickname) dado un identificador almacenado.
     * - En modo UUID: parsea el UUID y busca el nombre del OfflinePlayer.
     * - En modo NICKNAME: devuelve el identificador tal cual.
     * Nunca devuelve null; si no se puede resolver, devuelve el identificador original.
     */
    public static String resolveName(String identifier) {
        if (identifier == null) return "unknown";
        if (mode == Mode.UUID) {
            try {
                OfflinePlayer op = Bukkit.getOfflinePlayer(java.util.UUID.fromString(identifier));
                String name = op.getName();
                return name != null ? name : identifier;
            } catch (IllegalArgumentException e) {
                // El identificador no es un UUID válido; devolvemos tal cual como fallback
                return identifier;
            }
        }
        return identifier;
    }

    public static String normalize(String input) {
        // Si ya es un UUID válido y el modo es UUID, lo dejamos como está
        if (mode == Mode.UUID) {
            try {
                java.util.UUID.fromString(input);
                return input; // ya es UUID válido
            } catch (IllegalArgumentException ignored) {
                // No es UUID: lo tratamos como nombre y lo convertimos
                return fromName(input);
            }
        }
        // En modo NICKNAME simplemente devolvemos el input (el nombre)
        return input;
    }
}