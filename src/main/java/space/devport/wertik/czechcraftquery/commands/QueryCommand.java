package space.devport.wertik.czechcraftquery.commands;

import space.devport.utils.commands.MainCommand;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.subcommands.*;

public class QueryCommand extends MainCommand {

    public QueryCommand(QueryPlugin plugin) {
        super(plugin, "czechcraftquery");

        withSubCommand(new ReloadSubCommand(plugin));
        withSubCommand(new GetSubCommand(plugin));
        withSubCommand(new RequestSubCommand(plugin));
        withSubCommand(new ClearSubCommand(plugin));
        withSubCommand(new StartSubCommand(plugin));
        withSubCommand(new StopSubCommand(plugin));
        withSubCommand(new TestSubCommand(plugin));
        withSubCommand(new UpdateSubCommand(plugin));
    }

    @Override
    public String getDefaultUsage() {
        return "/%label%";
    }

    @Override
    public String getDefaultDescription() {
        return "Displays this.";
    }
}