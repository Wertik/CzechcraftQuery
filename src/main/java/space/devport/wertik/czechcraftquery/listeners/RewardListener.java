package space.devport.wertik.czechcraftquery.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.struct.Rewards;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.api.events.ServerAdvanceEvent;
import space.devport.wertik.czechcraftquery.api.events.ServerDropEvent;

public class RewardListener implements Listener {

    private final QueryPlugin plugin;

    private Rewards advanceRewards;
    private Rewards dropRewards;

    public RewardListener(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.advanceRewards = plugin.getConfiguration().getRewards("advance.rewards");
        this.dropRewards = plugin.getConfiguration().getRewards("drop.rewards");
    }

    @EventHandler
    public void onAdvance(ServerAdvanceEvent event) {
        ConsoleOutput.getInstance().debug("Caught event: " + event.getResponse().toString());

        if (!plugin.getConfig().getBoolean("advance.enabled", false)) return;

        advanceRewards.getPlaceholders()
                .add("%serverName%", event.getResponse().getServerName())
                .add("%serverPosition%", event.getResponse().getPosition())
                .add("%serverVotes%", event.getResponse().getVotes());
        advanceRewards.giveAll();
    }

    @EventHandler
    public void onDrop(ServerDropEvent event) {
        ConsoleOutput.getInstance().debug("Caught event: " + event.getResponse().toString());

        if (!plugin.getConfig().getBoolean("drop.enabled", false)) return;

        dropRewards.getPlaceholders()
                .add("%serverName%", event.getResponse().getServerName())
                .add("%serverPosition%", event.getResponse().getPosition())
                .add("%serverVotes%", event.getResponse().getVotes());
        dropRewards.giveAll();
    }
}