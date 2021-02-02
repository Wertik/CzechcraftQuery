package space.devport.wertik.czechcraftquery;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.DependencyUtil;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.czechcraftquery.commands.QueryCommand;
import space.devport.wertik.czechcraftquery.listeners.RewardListener;
import space.devport.wertik.czechcraftquery.listeners.VotifierListener;
import space.devport.wertik.czechcraftquery.system.RequestService;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.test.TestManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Log
public class QueryPlugin extends DevportPlugin {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");

    @Getter
    private static QueryPlugin instance;

    @Getter
    private final TestManager testManager = new TestManager(this);

    @Getter
    private final RequestService service = new RequestService(this);

    private VotifierListener votifierListener;
    private RewardListener rewardListener;

    private QueryExpansion expansion;

    @Getter
    private String durationFormat;

    @Override
    public void onPluginEnable() {
        instance = this;

        RequestType.initializeHandlers(this);

        testManager.load();

        new QueryLanguage(this).register();

        loadOptions();

        this.rewardListener = new RewardListener(this);
        rewardListener.load();

        registerListener(rewardListener);
        registerMainCommand(new QueryCommand(this));

        setupVotifierListener();
        registerPlaceholders();
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    private void loadOptions() {
        this.durationFormat = getConfig().getString("formats.duration", "HH:mm:ss");
    }

    @Override
    public void onPluginDisable() {
        unregisterPlaceholders();
        RequestType.clearHandlerCaches();
    }

    @Override
    public void onReload() {
        RequestType.reloadHandlers();

        testManager.load();

        registerPlaceholders();
        setupVotifierListener();

        loadOptions();

        rewardListener.load();
    }

    private void setupVotifierListener() {
        if (DependencyUtil.isEnabled("Votifier") && votifierListener == null) {
            this.votifierListener = new VotifierListener(this);
            registerListener(votifierListener);
            log.info("Registered Votifier listener.");
        }
    }

    // Attempt to unregister
    private void unregisterPlaceholders() {
        if (DependencyUtil.isEnabled("PlaceholderAPI") &&
                expansion != null &&
                VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1 &&
                expansion.isRegistered()) {

            expansion.unregister();
            log.log(DebugLevel.DEBUG, "Unregistered old placeholder expansion.");
        }
    }

    private void registerPlaceholders() {
        if (DependencyUtil.isEnabled("PlaceholderAPI")) {

            if (expansion == null)
                this.expansion = new QueryExpansion(this);

            unregisterPlaceholders();
            expansion.register();
            log.info("Registered placeholder expansion.");
        }
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE};
    }

    // Event shortcut
    public static void callEvent(Event event) {
        Bukkit.getScheduler().runTask(QueryPlugin.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(event);
            log.log(DebugLevel.DEBUG, String.format("Called event %s", event.getEventName()));
        });
    }
}
