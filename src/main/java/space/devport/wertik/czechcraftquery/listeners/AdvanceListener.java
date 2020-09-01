package space.devport.wertik.czechcraftquery.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.utils.struct.Rewards;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.api.events.CzechcraftServerAdvanceEvent;

public class AdvanceListener implements Listener {

    private final QueryPlugin plugin;

    private Rewards advanceRewards;

    public AdvanceListener(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.advanceRewards = plugin.getConfiguration().getRewards("advance.rewards");
    }

    @EventHandler
    public void onAdvance(CzechcraftServerAdvanceEvent event) {
        if (!plugin.getConfig().getBoolean("advance.enabled", false)) return;

        advanceRewards.getPlaceholders()
                .add("%serverName%", event.getResponse().getServerName())
                .add("%serverPosition%", event.getResponse().getPosition())
                .add("%serverVotes%", event.getResponse().getVotes());
        advanceRewards.giveAll();
    }
}