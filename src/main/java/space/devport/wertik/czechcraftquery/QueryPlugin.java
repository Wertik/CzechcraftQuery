package space.devport.wertik.czechcraftquery;

import lombok.Getter;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.wertik.czechcraftquery.commands.QueryCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.czechcraftquery.commands.subcommands.RequestSubCommand;
import space.devport.wertik.czechcraftquery.system.RequestService;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

import java.time.format.DateTimeFormatter;

public class QueryPlugin extends DevportPlugin {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    private RequestService service;

    @Override
    public void onPluginEnable() {
        this.service = new RequestService(this);

        RequestType.initializeHandlers(this);

        addMainCommand(new QueryCommand())
                .addSubCommand(new ReloadSubCommand(this))
                .addSubCommand(new RequestSubCommand(this));
    }

    @Override
    public void onPluginDisable() {

    }

    @Override
    public void onReload() {

    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION};
    }
}
