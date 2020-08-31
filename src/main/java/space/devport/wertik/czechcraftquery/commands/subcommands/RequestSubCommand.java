package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.CommandUtils;
import space.devport.wertik.czechcraftquery.commands.QuerySubCommand;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

public class RequestSubCommand extends QuerySubCommand {

    public RequestSubCommand(QueryPlugin plugin) {
        super(plugin, "request");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

        if (type == null) return CommandResult.FAILURE;

        RequestContext context = CommandUtils.parseContext(sender, args);

        if (!type.verifyContext(context)) {
            language.getPrefixed("Commands.Invalid-Context")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.FAILURE;
        }

        language.getPrefixed("Commands.Request.Sending")
                .replace("%type%", type.toString())
                .send(sender);

        type.getRequestHandler().sendRequest(context)
                .thenAcceptAsync((response) ->
                        language.getPrefixed("Commands.Request.Done")
                                .replace("%type%", type.toString())
                                .replace("%response%", response.toString())
                                .send(sender));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% request <type> <serverSlug> (username/me) (month)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Send a request for data with specified context. -f == force request.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(2, 4);
    }
}