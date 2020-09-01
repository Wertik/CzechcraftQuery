package space.devport.wertik.czechcraftquery.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.utils.text.message.Message;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.api.events.CzechcraftServerAdvanceEvent;

public class AdvanceListener implements Listener {

    private final QueryPlugin plugin;

    public AdvanceListener(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvance(CzechcraftServerAdvanceEvent event) {
        if (!plugin.getConfig().getBoolean("advance.enabled", false)) return;

        Message broadcast = plugin.getConfiguration().getMessage("advance.broadcast", new Message())
                .replace("%serverName%", event.getResponse().getServerName())
                .replace("%serverPosition%", event.getResponse().getPosition())
                .replace("%serverVotes%", event.getResponse().getVotes());

        Bukkit.getOnlinePlayers().forEach(broadcast::send);
    }
}