package space.devport.wertik.czechcraftquery.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.utils.logging.DebugLevel;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

@Log
public class VotifierListener implements Listener {

    private final QueryPlugin plugin;

    public VotifierListener(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {

        if (!plugin.getConfig().getBoolean("force-update-on-vote", false))
            return;

        log.log(DebugLevel.DEBUG, String.format("Received a vote: %s", event.getVote().toString()));

        // Capture only votes from Czech-Craft.eu
        if (!event.getVote().getServiceName().equalsIgnoreCase("Czech-Craft.eu"))
            return;

        RequestContext context = new RequestContext(plugin.getConfig().getString("server-slug"), event.getVote().getUsername());
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> RequestType.updateResponsesForContext(context), 20L);
    }
}