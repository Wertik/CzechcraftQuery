package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.CommandUtils;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

public class StartSubCommand extends SubCommand {

    private final QueryPlugin plugin;

    public StartSubCommand(QueryPlugin plugin) {
        super("start");
        this.plugin = plugin;
        this.preconditions = new Preconditions()
                .permissions("czechcraftquery.start");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

            if (type == null) return CommandResult.FAILURE;

            type.getRequestHandler().start();
            language.getPrefixed("Commands.Start.Done-Single")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.SUCCESS;
        }

        for (RequestType type : RequestType.values()) {
            type.getRequestHandler().start();
        }

        language.sendPrefixed(sender, "Commands.Start.Done");
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% start (type)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Start a type request handler update task.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}