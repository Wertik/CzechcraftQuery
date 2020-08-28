package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RequestHandler<T extends AbstractResponse> implements Runnable, Listener {

    private final QueryPlugin plugin;

    private final RequestType requestType;

    private final Map<RequestContext, T> cache = new HashMap<>();

    // Update task
    private int refreshInterval;
    private BukkitTask task;

    public RequestHandler(QueryPlugin plugin, RequestType requestType) {
        this.plugin = plugin;
        this.requestType = requestType;
    }

    public void loadOptions() {
        this.refreshInterval = plugin.getConfig().getInt("refresh-rates." + requestType.toString().toLowerCase(), 20);
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
    public T getResponse(RequestContext context) {
        if (this.cache.containsKey(context))
            return this.cache.get(context);

        return sendRequest(context);
    }

    /**
     * Send a request and cache the response.
     *
     * @param context RequestContext
     */
    public T sendRequest(RequestContext context) {
        JsonObject jsonResponse = plugin.getService().sendRequest(requestType, context);
        T response = (T) requestType.getParser().parse(jsonResponse);
        this.cache.put(context, response);
        return response;
    }

    // Update all cached values.
    @Override
    public void run() {

        // Update all cached values
        for (RequestContext context : new HashSet<>(this.cache.keySet())) {
            sendRequest(context);
        }

        plugin.getConsoleOutput().debug("Updated all cached values.");
    }

    // Remove all player cached values when he leaves.
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        for (RequestContext context : new HashSet<>(this.cache.keySet())) {
            if (context.getUserName().equalsIgnoreCase(event.getPlayer().getName())) {
                this.cache.remove(context);
            }
        }
    }
}