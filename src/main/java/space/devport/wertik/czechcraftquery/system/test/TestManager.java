package space.devport.wertik.czechcraftquery.system.test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.extern.java.Log;
import space.devport.utils.logging.DebugLevel;
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

@Log
public class TestManager {

    private final QueryPlugin plugin;

    private final Map<String, JsonObject> loadedResponses = new HashMap<>();

    public TestManager(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        File testDirectory = new File(plugin.getDataFolder(), "/tests/");

        // Create defaults.
        if (!testDirectory.exists()) {
            testDirectory.mkdirs();

            plugin.saveResource("tests/test1.json", false);
            plugin.saveResource("tests/test2.json", false);
        }

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

            loadedResponses.put(name, jsonObject);
            log.log(DebugLevel.DEBUG, String.format("Loaded test response %s", name));
        }

        if (!loadedResponses.isEmpty())
            log.info(String.format("Loaded %d test response(s)...", loadedResponses.size()));
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

        log.log(DebugLevel.DEBUG, String.format("Test response: %s", ShortenUtil.shortenString(jsonResponse)));

        JsonParser jsonParser = new JsonParser();

        JsonElement element;
        try {
            element = jsonParser.parse(jsonResponse);
        } catch (JsonSyntaxException e) {
            log.warning(String.format("Failed to parse test response (%s) : %s", ShortenUtil.shortenString(jsonResponse), e.getMessage()));
            e.printStackTrace();
            return null;
        }

        if (!element.isJsonObject())
            return null;

        return element.getAsJsonObject();
    }
}