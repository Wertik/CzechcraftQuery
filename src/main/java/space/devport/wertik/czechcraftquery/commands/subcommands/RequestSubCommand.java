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
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

public class RequestSubCommand extends SubCommand {

    private final QueryPlugin plugin;

    public RequestSubCommand(QueryPlugin plugin) {
        super("request");
        this.plugin = plugin;
        this.preconditions = new Preconditions()
                .permissions("czechcraftquery.request");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

        if (type == null) return CommandResult.FAILURE;

        String serverSlug = args[1];

        RequestContext context = new RequestContext(serverSlug);

        if (args.length > 2) {
            String month = CommandUtils.attemptParseMonth(args[2]);
            String username = null;

            if (month == null) {
                username = CommandUtils.attemptParseUsername(sender, args[2]);
                if (args.length > 3)
                    month = CommandUtils.attemptParseMonth(args[3]);
            } else {
                if (args.length > 3)
                    username = CommandUtils.attemptParseUsername(sender, args[3]);
            }

            context.setMonth(month);
            context.setUserName(username);
        }

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