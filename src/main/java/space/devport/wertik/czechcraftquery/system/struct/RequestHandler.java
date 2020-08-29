package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.BlankResponse;

import java.util.*;
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
        this.cache.clear();
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

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 20L, refreshInterval);
        plugin.getConsoleOutput().debug("Started RequestHandler update task for " + requestType.toString());
    }

    /**
     * Get a response from cache, or fetch a new one and cache it.
     *
     * @param context The request context
     */
    public CompletableFuture<AbstractResponse> getResponse(RequestContext context) {
        if (this.cache.containsKey(context))
            return CompletableFuture.supplyAsync(() -> this.cache.get(context));

        return sendRequest(context);
    }

    /**
     * Send a request and cache the response.
     *
     * @param context RequestContext
     */
    public CompletableFuture<AbstractResponse> sendRequest(RequestContext context) {

        if (!requestType.verifyContext(context)) return null;

        CompletableFuture<JsonObject> future = plugin.getService().sendRequest(context.parse(requestType.getStringURL()));

        return future.thenApplyAsync((jsonResponse) -> {
            AbstractResponse response = requestType.parse(jsonResponse);
            Validate.notNull(response, "Response could not be parsed.");
            this.cache.put(context, response);
            return response;
        }).exceptionally((e) -> {
            if (plugin.getConsoleOutput().isDebug())
                e.printStackTrace();
            return new BlankResponse(e.getMessage());
        });
    }

    @Override
    public void run() {

        Set<String> onlinePlayers = Bukkit.getOnlinePlayers()
                .stream().map(Player::getName)
                .collect(Collectors.toSet());

        plugin.getConsoleOutput().debug("Updating all cached values.");

        for (RequestContext context : new HashSet<>(this.cache.keySet())) {

            // Player disconnected
            if (context.getUserName() != null && !onlinePlayers.contains(context.getUserName())) {
                this.cache.remove(context);
                plugin.getConsoleOutput().debug("Removed context " + context.toString());
                continue;
            }

            sendRequest(context);
        }
    }

    public boolean isRunning() {
        return this.task != null;
    }

    public Map<RequestContext, AbstractResponse> getCache() {
        return Collections.unmodifiableMap(cache);
    }
}