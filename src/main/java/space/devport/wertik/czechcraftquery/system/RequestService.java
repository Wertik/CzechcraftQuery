package space.devport.wertik.czechcraftquery.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.ShortenUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class RequestService {

    private final QueryPlugin plugin;

    private final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

    public RequestService(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<JsonObject> sendRequest(String stringURL) {

        ListenableFuture<Response> getFuture;

        try {
            getFuture = asyncHttpClient.prepareGet(stringURL).execute();
        } catch (IllegalArgumentException e) {
            plugin.getConsoleOutput().err("Could not prepare request from url " + stringURL);
            if (plugin.getConsoleOutput().isDebug())
                e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> null);
        }

        return getFuture.toCompletableFuture().thenApplyAsync((response) -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));

            String jsonResponse = reader.lines().collect(Collectors.joining(""));

            try {
                reader.close();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            plugin.getConsoleOutput().debug("Caught response: " + ShortenUtil.shortenString(jsonResponse) + " from URL " + stringURL);

            JsonParser jsonParser = new JsonParser();

            JsonElement element;
            try {
                element = jsonParser.parse(jsonResponse);
            } catch (JsonSyntaxException e) {
                throw new CompletionException(e);
            }

            if (!element.isJsonObject()) return null;

            return element.getAsJsonObject();
        });
    }
}