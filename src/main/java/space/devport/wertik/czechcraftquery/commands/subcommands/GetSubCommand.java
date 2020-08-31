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
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

import java.util.concurrent.CompletableFuture;

public class GetSubCommand extends QuerySubCommand {

    public GetSubCommand(QueryPlugin plugin) {
        super(plugin, "get");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        RequestType type = CommandUtils.parseRequestType(sender, language, args[0]);

        if (type == null)
            return CommandResult.FAILURE;

        RequestContext context = CommandUtils.parseContext(sender, args);

        if (!type.verifyContext(context)) {
            language.getPrefixed("Commands.Invalid-Context")
                    .replace("%type%", type.toString())
                    .send(sender);
            return CommandResult.FAILURE;
        }

        CompletableFuture<AbstractResponse> responseFuture = type.getRequestHandler().getResponse(context);
        responseFuture.thenAcceptAsync((response) ->
                language.getPrefixed("Commands.Get.Done")
                        .replace("%type%", type.toString())
                        .replace("%response%", response.toString())
                        .send(sender));
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% get <type> (serverSlug) (username) (month)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Get a response based on context.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1, 4);
    }
}