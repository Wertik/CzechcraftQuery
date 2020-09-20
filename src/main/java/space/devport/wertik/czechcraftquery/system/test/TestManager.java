package space.devport.wertik.czechcraftquery.system.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.ShortenUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TestManager {

    private final QueryPlugin plugin;

    private final Map<String, JsonObject> loadedResponses = new HashMap<>();

    public TestManager(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File testDirectory = new File(plugin.getDataFolder(), "/tests/");

        // We don't mind.
        if (!testDirectory.exists()) return;

        for (File file : testDirectory.listFiles()) {

            if (!file.getName().endsWith(".json"))
                continue;

            JsonObject jsonObject;
            try {
                jsonObject = parseResponse(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            String name = file.getName().replace(".json", "");

            this.loadedResponses.put(name, jsonObject);
            plugin.getConsoleOutput().debug("Loaded test response " + name);
        }

        if (!this.loadedResponses.isEmpty())
            plugin.getConsoleOutput().info("Loaded " + this.loadedResponses.size() + " test response(s)...");
    }

    public JsonObject getTest(String name) {
        return this.loadedResponses.get(name);
    }

    public boolean hasTest(String name) {
        return this.loadedResponses.containsKey(name);
    }

    private JsonObject parseResponse(InputStreamReader input) {

        BufferedReader reader = new BufferedReader(input);

        String jsonResponse = reader.lines().collect(Collectors.joining(""));

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        plugin.getConsoleOutput().debug("Parsed test response: " + ShortenUtil.shortenString(jsonResponse));

        JsonParser jsonParser = new JsonParser();

        JsonElement element;
        try {
            element = jsonParser.parse(jsonResponse);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }

        if (!element.isJsonObject())
            return null;

        return element.getAsJsonObject();
    }
}