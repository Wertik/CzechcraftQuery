package space.devport.wertik.czechcraftquery.system;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.extern.java.Log;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import space.devport.utils.logging.DebugLevel;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.ShortenUtil;
import space.devport.wertik.czechcraftquery.exception.ErrorResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Log
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
            log.severe(String.format("Could not prepare request from url (%s): %s", stringURL, e.getMessage()));
            e.printStackTrace();
            return CompletableFuture.supplyAsync(() -> null);
        }

        return getFuture.toCompletableFuture().thenApplyAsync((response) -> {

            log.log(DebugLevel.DEBUG, String.format("Response code: %d", response.getStatusCode()));

            if (response.getStatusCode() != 200)
                throw new ErrorResponseException(response.getStatusText());

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));

            String jsonResponse = reader.lines().collect(Collectors.joining(""));

            try {
                reader.close();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            log.log(DebugLevel.DEBUG, "Caught response: " + ShortenUtil.shortenString(jsonResponse) + " from URL " + stringURL);

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