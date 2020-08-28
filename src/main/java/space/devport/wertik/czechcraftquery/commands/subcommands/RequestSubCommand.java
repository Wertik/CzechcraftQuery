package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RequestSubCommand extends SubCommand {

    private final QueryPlugin plugin;

    private final DateFormat monthFormat = new SimpleDateFormat("yyyy/MM");

    public RequestSubCommand(QueryPlugin plugin) {
        super("request");
        this.plugin = plugin;
        this.preconditions = new Preconditions()
                .permissions("czechcraftquery.request");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        RequestType type = RequestType.fromString(args[0]);
        if (type == null) {
            language.getPrefixed("Commands.Invalid-Type")
                    .replace("%param%", args[0])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        String serverSlug = args[1];

        RequestContext context = new RequestContext(serverSlug);

        if (args.length > 2) {
            String month = attemptParseMonth(args[2]);
            String username;

            if (month == null)
                username = attemptParseUsername(sender, args[2]);
            else
                username = attemptParseUsername(sender, args[3]);

            context.setMonth(month);
            context.setUserName(username);
        }

        if (!type.verifyContext(context)) {
            language.getPrefixed("Commands.Invalid-Context")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.FAILURE;
        }

        AbstractResponse response = type.getRequestHandler().getResponse(context);
        language.getPrefixed("Commands.Request.Done")
                .replace("%type%", type.toString())
                .replace("%response%", response.toString())
                .send(sender);
        return CommandResult.SUCCESS;
    }

    private String attemptParseMonth(String input) {
        try {
            monthFormat.parse(input);
            return input;
        } catch (ParseException e) {
            return null;
        }
    }

    private String attemptParseUsername(CommandSender sender, String input) {
        String username;
        if (input.equalsIgnoreCase("me")) {
            if (!(sender instanceof Player)) return null;

            username = sender.getName();
        } else username = input;
        return username;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% request <type> <serverSlug> (username/me) (month)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Send a request for data with specified context.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(2, 4);
    }
}