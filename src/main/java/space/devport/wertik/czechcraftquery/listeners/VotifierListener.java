package space.devport.wertik.czechcraftquery.listeners;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

public class VotifierListener implements Listener {

    private final QueryPlugin plugin;

    public VotifierListener(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {

        if (!plugin.getConfig().getBoolean("force-update-on-vote", false)) return;

        plugin.getConsoleOutput().debug("Caught a votifier event! Vote: " + event.getVote().toString());

        if (!event.getVote().getServiceName().equalsIgnoreCase("Czech-Craft.eu"))
            return;

        RequestContext context = new RequestContext(plugin.getConfig().getString("server-slug", "pvpcraft"), event.getVote().getUsername());
        RequestType.updateResponses(context);
        plugin.getConsoleOutput().debug("Updated all responses.");
    }
}