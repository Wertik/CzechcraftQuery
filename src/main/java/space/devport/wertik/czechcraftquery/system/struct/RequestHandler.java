package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.exception.ErrorResponseException;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.invalid.BlankResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.invalid.ErrorResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable {

    private final QueryPlugin plugin;

    private final RequestType requestType;

    private final Map<RequestContext, AbstractResponse> cache = new HashMap<>();

    // Update task
    private int refreshInterval;
    private BukkitTask task;

    public RequestHandler(QueryPlugin plugin, RequestType requestType) {
        this.plugin = plugin;
        this.requestType = requestType;
    }

    public void clearCache() {
        int size = this.cache.size();
        this.cache.clear();
        if (size != 0)
            plugin.getConsoleOutput().debug("Cleared handler cache for " + requestType.toString() + " (" + size + ")");
    }

    public void loadOptions() {
        this.refreshInterval = plugin.getConfig().getInt("refresh-rates." + requestType.toString().toLowerCase(), 300) * 20;
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    public void start() {
        if (task != null)
            stop();

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, refreshInterval, refreshInterval);
        plugin.getConsoleOutput().debug("Started RequestHandler update task for " + requestType.toString());
    }

    /**
     * Get a response from cache, or fetch a new one and cache it.
     *
     * @param context The request context
     */
    public CompletableFuture<AbstractResponse> getResponse(RequestContext context) {
        final RequestContext finalContext = requestType.stripContext(context);

        if (this.cache.containsKey(finalContext))
            return CompletableFuture.supplyAsync(() -> this.cache.get(finalContext));

        return sendRequest(finalContext);
    }

    /**
     * Send a request and cache the response.
     *
     * @param context RequestContext
     */
    public CompletableFuture<AbstractResponse> sendRequest(RequestContext context) {

        final RequestContext finalContext = requestType.stripContext(context);

        if (!requestType.verifyContext(finalContext))
            return CompletableFuture.supplyAsync(() -> new BlankResponse("Invalid context."));

        CompletableFuture<JsonObject> future = plugin.getService().sendRequest(finalContext.parse(requestType.getStringURL()));

        return acceptResponse(context, future);
    }

    /**
     * Parse and cache a test response.
     */
    public CompletableFuture<AbstractResponse> acceptTestResponse(RequestContext context, JsonObject jsonObject) {
        final RequestContext finalContext = requestType.stripContext(context);

        if (!requestType.verifyContext(finalContext))
            return CompletableFuture.supplyAsync(() -> new BlankResponse("Invalid context."));

        return acceptResponse(context, CompletableFuture.supplyAsync(() -> jsonObject));
    }

    /**
     * Accept a Json response from a request and cache it based on context provided.
     */
    public CompletableFuture<AbstractResponse> acceptResponse(RequestContext context, CompletableFuture<JsonObject> future) {
        return future.thenApplyAsync((jsonResponse) -> {
            AbstractResponse response = requestType.parse(jsonResponse);
            Validate.notNull(response, "Response from context " + context.toString() + " could not be parsed.");
            cacheResponse(context, response);
            return response;
        }).exceptionally((exception) -> {

            if (exception instanceof ErrorResponseException) {
                ErrorResponseException errorResponseException = (ErrorResponseException) exception;
                plugin.getConsoleOutput().warn("Could not fetch data from API, response: " + errorResponseException.getResponseText());
                return new ErrorResponse(errorResponseException.getResponseText());
            }

            plugin.getConsoleOutput().err(exception.getMessage());
            if (plugin.getConsoleOutput().isDebug())
                exception.printStackTrace();
            return new BlankResponse(exception.getCause().getClass().getSimpleName());
        });
    }

    private void cacheResponse(RequestContext context, AbstractResponse response) {
        requestType.getResponseListener().listen(this.cache.get(context), response);
        this.cache.put(context, response);
    }

    /**
     * Update cached value based on context.
     * Request is sent only if there's a response already stored with this context.
     */
    public void updateResponse(RequestContext context) {
        RequestContext strippedContext = requestType.stripContext(context);
        if (this.cache.containsKey(strippedContext))
            sendRequest(strippedContext);
    }

    public void updateResponses() {

        if (this.cache.isEmpty()) return;

        plugin.getConsoleOutput().debug("Updating all cached values for type " + requestType.toString() + " (" + this.cache.size() + ") ...");

        Set<String> onlinePlayers = Bukkit.getOnlinePlayers()
                .stream().map(Player::getName)
                .collect(Collectors.toSet());

        for (RequestContext context : new HashSet<>(this.cache.keySet())) {

            // Player disconnected
            if (context.getUserName() != null && !onlinePlayers.contains(context.getUserName())) {
                this.cache.remove(context);
                plugin.getConsoleOutput().debug("Removed context " + context.toString() + " in type " + requestType.toString());
                continue;
            }

            sendRequest(context);
        }
    }

    @Override
    public void run() {
        updateResponses();
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public Map<RequestContext, AbstractResponse> getCache() {
        return Collections.unmodifiableMap(cache);
    }
}