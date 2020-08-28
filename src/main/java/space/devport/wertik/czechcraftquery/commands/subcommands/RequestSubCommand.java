package space.devport.wertik.czechcraftquery.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;
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
        ServerInfoResponse response = (ServerInfoResponse) RequestType.SERVER_INFO.getRequestHandler().getResponse(new RequestContext("pvpcraft"));
        sender.sendMessage(response.toString());

        NextVoteResponse nextVoteResponse = (NextVoteResponse) RequestType.NEXT_VOTE.getRequestHandler().getResponse(new RequestContext("pvpcraft", "Wertik1206"));
        sender.sendMessage(nextVoteResponse.toString());
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