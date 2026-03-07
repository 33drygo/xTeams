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

    public static String isUpdateAvailable(String currentVersion) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            JSONParser parser = new JSONParser();
            JSONArray response = (JSONArray) parser.parse(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
            );

            if (response.isEmpty()) return "false";

            JSONObject latestVersion = (JSONObject) response.get(0);
            String latestVersionNumber = (String) latestVersion.get("version_number");

            return !currentVersion.equals(latestVersionNumber) ? latestVersionNumber : "false";

        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }
}
