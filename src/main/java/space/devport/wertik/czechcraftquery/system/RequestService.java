package space.devport.wertik.czechcraftquery.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RequestService {

    private final QueryPlugin plugin;

    private final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

    public RequestService(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<JsonObject> sendRequest(RequestType type, RequestContext context) {

        ListenableFuture<Response> getFuture = asyncHttpClient.prepareGet(context.parse(type.getStringURL())).execute();

        return getFuture.toCompletableFuture().thenApplyAsync((response) -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));

            String jsonResponse = reader.lines().collect(Collectors.joining(""));

            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            plugin.getConsoleOutput().debug("Caught response: " + jsonResponse);

            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(jsonResponse);

            if (!element.isJsonObject()) return null;

            return element.getAsJsonObject();
        });
    }
}