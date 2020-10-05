package space.devport.wertik.czechcraftquery;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.czechcraftquery.commands.QueryCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.ClearSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.GetSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.RequestSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.StartSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.StopSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.TestSubCommand;
import space.devport.wertik.czechcraftquery.listeners.RewardListener;
import space.devport.wertik.czechcraftquery.listeners.VotifierListener;
import space.devport.wertik.czechcraftquery.system.RequestService;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.test.TestManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class QueryPlugin extends DevportPlugin {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");

    @Getter
    private TestManager testManager;

    @Getter
    private String durationFormat;

    @Getter
    private RequestService service;

    private VotifierListener votifierListener;
    private RewardListener rewardListener;

    private QueryPlaceholders placeholders;

    @Override
    public void onPluginEnable() {
        this.service = new RequestService(this);

        RequestType.initializeHandlers(this);

        this.testManager = new TestManager(this);
        this.testManager.load();

        new QueryLanguage(this);

        loadOptions();

        this.rewardListener = new RewardListener(this);
        this.rewardListener.load();
        registerListener(this.rewardListener);

        addMainCommand(new QueryCommand())
                .addSubCommand(new ReloadSubCommand(this))
                .addSubCommand(new GetSubCommand(this))
                .addSubCommand(new RequestSubCommand(this))
                .addSubCommand(new ClearSubCommand(this))
                .addSubCommand(new StartSubCommand(this))
                .addSubCommand(new StopSubCommand(this))
                .addSubCommand(new TestSubCommand(this));

        setupVotifier();
        setupPlaceholders();
    }

    private void loadOptions() {
        this.durationFormat = getConfig().getString("formats.duration", "HH:mm:ss");
    }

    @Override
    public void onPluginDisable() {
        unregisterPlaceholders();
        RequestType.clearHandlerCaches(this);
    }

    @Override
    public void onReload() {
        RequestType.reloadHandlers(this);

        this.testManager.load();

        setupPlaceholders();
        setupVotifier();

        loadOptions();

        this.rewardListener.load();
    }

    private void setupVotifier() {
        if (getPluginManager().isPluginEnabled("Votifier") && this.votifierListener == null) {
            this.votifierListener = new VotifierListener(this);
            registerListener(votifierListener);
            consoleOutput.info("Registered Votifier listener.");
        }
    }

    // Attempt to unregister
    private void unregisterPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") &&
                this.placeholders != null &&
                VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1) {

            if (this.placeholders.isRegistered()) {
                this.placeholders.unregister();
                consoleOutput.debug("Unregistered placeholder expansion.");
            }
        }
    }

    private void setupPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {

            if (placeholders == null)
                this.placeholders = new QueryPlaceholders(this);

            unregisterPlaceholders();
            this.placeholders.register();
            consoleOutput.info("Registered placeholder expansion.");
        }
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE};
    }

    // Event shortcut
    public static void callEvent(Event event) {
        Bukkit.getScheduler().runTask(QueryPlugin.getInstance(), () -> {
            ConsoleOutput.getInstance().debug("Called event " + event.getEventName());
            Bukkit.getPluginManager().callEvent(event);
        });
    }
}
