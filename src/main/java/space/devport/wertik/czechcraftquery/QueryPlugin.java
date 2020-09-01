package space.devport.wertik.czechcraftquery;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.czechcraftquery.commands.QueryCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.*;
import space.devport.wertik.czechcraftquery.listeners.AdvanceListener;
import space.devport.wertik.czechcraftquery.listeners.VotifierListener;
import space.devport.wertik.czechcraftquery.system.RequestService;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class QueryPlugin extends DevportPlugin {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy/MM");

    @Getter
    private String durationFormat;

    @Getter
    private RequestService service;

    private VotifierListener votifierListener;

    private QueryPlaceholders placeholders;

    @Override
    public void onPluginEnable() {
        this.service = new RequestService(this);

        RequestType.initializeHandlers(this);

        new QueryLanguage();

        loadOptions();

        registerListener(new AdvanceListener(this));

        addMainCommand(new QueryCommand())
                .addSubCommand(new ReloadSubCommand(this))
                .addSubCommand(new GetSubCommand(this))
                .addSubCommand(new RequestSubCommand(this))
                .addSubCommand(new ClearSubCommand(this))
                .addSubCommand(new StartSubCommand(this))
                .addSubCommand(new StopSubCommand(this));

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
        setupPlaceholders();
        setupVotifier();
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
}
