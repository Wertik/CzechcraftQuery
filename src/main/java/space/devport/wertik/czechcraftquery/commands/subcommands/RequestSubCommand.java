package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.ContextURL;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

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
        ServerInfoResponse response = (ServerInfoResponse) RequestType.SERVER_INFO.getRequestHandler().sendRequest(new ContextURL("pvpcraft"));
        sender.sendMessage(response.getServerName() + " has " + response.getVotes() + " votes.");
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% request";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Send a test request.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(0);
    }
}