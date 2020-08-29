package space.devport.wertik.czechcraftquery;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.wertik.czechcraftquery.commands.QueryCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.*;
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

    private void setupPlaceholders() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {

            if (placeholders == null)
                this.placeholders = new QueryPlaceholders(this);

            // Attempt to unregister
            if (VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1) {
                if (this.placeholders.isRegistered()) {
                    this.placeholders.unregister();
                    consoleOutput.debug("Unregistered expansion");
                }
            }

            this.placeholders.register();
            consoleOutput.info("Found PlaceholderAPI! Registered expansion.");
        }
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.LANGUAGE};
    }
}
