package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.CommandUtils;
import space.devport.wertik.czechcraftquery.commands.QuerySubCommand;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;

public class UpdateSubCommand extends QuerySubCommand {

    public UpdateSubCommand(QueryPlugin plugin) {
        super(plugin, "update");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        if (args.length > 0) {
            RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

            if (type == null) return CommandResult.FAILURE;

            type.getRequestHandler().updateResponses();
            language.getPrefixed("Commands.Update.Done-Single")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.SUCCESS;
        }

        for (RequestType type : RequestType.values()) {
            type.getRequestHandler().updateResponses();
        }

        language.sendPrefixed(sender, "Commands.Update.Done");
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% update (type)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Call for an update of a type, or all of them.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(0, 1);
    }
}
