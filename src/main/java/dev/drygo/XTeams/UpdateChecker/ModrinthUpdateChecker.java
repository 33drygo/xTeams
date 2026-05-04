package dev.drygo.XTeams.UpdateChecker;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ModrinthUpdateChecker {
    private static final String apiUrl = "https://api.modrinth.com/v2/project/E4NdaHMh/version";

    /**
     * Devuelve el número de la última versión publicada si es estrictamente mayor
     * que la actual, o "false" si está al día / no se pudo consultar.
     * Bloquea I/O — debe llamarse desde un hilo asíncrono.
     */
    public static String isUpdateAvailable(String currentVersion) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            JSONParser parser = new JSONParser();
            JSONArray response;
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                response = (JSONArray) parser.parse(reader);
            }

            if (response.isEmpty()) return "false";

            JSONObject latestVersion = (JSONObject) response.get(0);
            String latestVersionNumber = (String) latestVersion.get("version_number");

            return isNewer(latestVersionNumber, currentVersion) ? latestVersionNumber : "false";

        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Compara dos cadenas de versión semántica (ej. "1.3.26" vs "1.3.2").
     * Devuelve true si {@code candidate} es estrictamente mayor que {@code current}.
     * Cualquier segmento no numérico se trata como 0 para no romper la comparación.
     */
    private static boolean isNewer(String candidate, String current) {
        if (candidate == null || current == null) return false;
        String[] a = candidate.split("\\.");
        String[] b = current.split("\\.");
        int len = Math.max(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int x = i < a.length ? parseSafe(a[i]) : 0;
            int y = i < b.length ? parseSafe(b[i]) : 0;
            if (x != y) return x > y;
        }
        return false;
    }

    private static int parseSafe(String s) {
        try {
            return Integer.parseInt(s.replaceAll("\\D.*$", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
