package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.QuerySubCommand;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

public class ClearSubCommand extends QuerySubCommand {

    public ClearSubCommand(QueryPlugin plugin) {
        super(plugin, "clear");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            RequestType type = RequestType.fromString(args[0]);

            if (type == null) {
                language.getPrefixed("Commands.Invalid-Type")
                        .replace("%param%", args[0])
                        .send(sender);
                return CommandResult.FAILURE;
            }

            int count = type.getRequestHandler().getCache().size();
            type.getRequestHandler().clearCache();
            language.getPrefixed("Commands.Clear.Done-Single")
                    .replace("%type%", type.toString())
                    .replace("%count%", count)
                    .send(sender);
            return CommandResult.SUCCESS;
        }

        int count = 0;
        for (RequestType type : RequestType.values()) {
            count += type.getRequestHandler().getCache().size();
            type.getRequestHandler().clearCache();
        }

        language.getPrefixed("Commands.Clear.Done")
                .replace("%count%", count)
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% clear (type)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Clear response cache for type, or all.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}