package space.devport.wertik.czechcraftquery.commands.subcommands;

import com.google.gson.JsonObject;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.commands.CommandUtils;
import space.devport.wertik.czechcraftquery.commands.QuerySubCommand;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;

import java.util.Arrays;

public class TestSubCommand extends QuerySubCommand {

    public TestSubCommand(QueryPlugin plugin) {
        super(plugin, "test");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        RequestType requestType = CommandUtils.parseRequestType(sender, language, args[0]);

        if (requestType == null) return CommandResult.FAILURE;

        String fileName = args[1].replace(".json", "");

        if (!getPlugin().getTestManager().hasTest(fileName)) {
            language.getPrefixed("commands.Test.Invalid-Test")
                    .replace("%param%", args[1])
                    .send(sender);
            return CommandResult.FAILURE;
        }

        RequestContext context = CommandUtils.parseContext(sender, Arrays.copyOfRange(args, 2, args.length));

        if (!requestType.verifyContext(context)) {
            language.getPrefixed("Commands.Invalid-Context")
                    .replace("%type%", requestType.toString())
                    .send(sender);
            return CommandResult.FAILURE;
        }

        JsonObject jsonObject = plugin.getTestManager().getTest(fileName);

        requestType.getRequestHandler().acceptTestResponse(context, jsonObject).thenAcceptAsync((response) -> {
            language.getPrefixed("Commands.Test.Done")
                    .replace("%response%", response.toString())
                    .replace("%type%", requestType.toString())
                    .replace("%context%", context.toString())
                    .send(sender);
        });
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable String getDefaultUsage() {
        return "/%label% test <requestType> <fileName> <serverSlug> (username) (month)";
    }

    @Override
    public @Nullable String getDefaultDescription() {
        return "Process and cache a test response from the /tests directory.";
    }

    @Override
    public @Nullable ArgumentRange getRange() {
        return new ArgumentRange(3, 5);
    }
}