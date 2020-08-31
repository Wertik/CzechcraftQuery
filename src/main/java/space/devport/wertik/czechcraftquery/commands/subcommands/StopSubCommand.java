package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.CommandUtils;
import space.devport.wertik.czechcraftquery.commands.QuerySubCommand;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

public class StopSubCommand extends QuerySubCommand {

    public StopSubCommand(QueryPlugin plugin) {
        super(plugin, "stop");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length > 0) {
            RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

            if (type == null) return CommandResult.FAILURE;

            if (!type.getRequestHandler().isRunning()) {
                language.getPrefixed("Commands.Stop.Not-Running")
                        .replace("%type%", type.toString())
                        .send(sender);
                return CommandResult.FAILURE;
            }

            type.getRequestHandler().stop();
            language.getPrefixed("Commands.Stop.Done-Single")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.SUCCESS;
        }

        for (RequestType type : RequestType.values()) {
            type.getRequestHandler().stop();
        }

        language.sendPrefixed(sender, "Commands.Stop.Done");
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% stop (type)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Stop a type handler update task.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}