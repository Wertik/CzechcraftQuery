package space.devport.wertik.czechcraftquery.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class RequestService {

    private final QueryPlugin plugin;

    public RequestService(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    private URL createURL(String stringURL, RequestContext context) {
        try {
            return new URL(context.parse(stringURL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpURLConnection openConnection(String stringURL, RequestContext context) {
        URL url = createURL(stringURL, context);

        if (url == null) {
            plugin.getConsoleOutput().err("Could not create URL from " + stringURL);
            return null;
        }

        try {
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject sendRequest(RequestType type, RequestContext context) {
        HttpURLConnection connection = openConnection(type.getStringURL(), context);
        try {
            connection.setRequestMethod("GET");
            int response = connection.getResponseCode();
            plugin.getConsoleOutput().debug("Response code from " + type.toString() + ":" + response);

            if (response != HttpURLConnection.HTTP_OK) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String jsonResponse = reader.lines().collect(Collectors.joining(""));
            reader.close();

            plugin.getConsoleOutput().debug("Caught response: " + jsonResponse);
            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(jsonResponse);
            if (!element.isJsonObject()) return null;
            return element.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}