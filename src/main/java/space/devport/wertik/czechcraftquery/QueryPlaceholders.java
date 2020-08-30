package space.devport.wertik.czechcraftquery;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class QueryPlaceholders extends PlaceholderExpansion {

    private final QueryPlugin plugin;

    public QueryPlaceholders(QueryPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "czechcraft";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /*
     * %czechcraft_<type>_<serverSlug>_....%
     * */

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        String[] args = params.split("_");

        if (args.length <= 1) {
            return "not_enough_args";
        }

        RequestType type = RequestType.fromString(args[0]);

        if (type == null) return "invalid_type";

        RequestContext context = new RequestContext(args[1]);

        if (player != null)
            context.setUserName(player.getName());

        AbstractResponse response = type.getRequestHandler().getResponse(context).join();

        if (!response.isValid())
            return "no_response";

        switch (type) {
            /*
             * _name%
             * _votes%
             * _position%
             * _address%
             * _name%
             * */
            case SERVER_INFO:
                if (args.length < 3) return "not_enough_args";

                ServerInfoResponse serverInfoResponse = (ServerInfoResponse) response;

                if (args[2].equalsIgnoreCase("name")) {
                    return serverInfoResponse.getServerName();
                } else if (args[2].equalsIgnoreCase("address")) {
                    return serverInfoResponse.getAddress();
                } else if (args[2].equalsIgnoreCase("position")) {
                    return String.valueOf(serverInfoResponse.getPosition());
                } else if (args[2].equalsIgnoreCase("votes")) {
                    return String.valueOf(serverInfoResponse.getVotes());
                }
                break;
            /*
             * _canvote%
             * _until%
             * */
            case NEXT_VOTE:
                if (args.length < 3) return "not_enough_args";

                NextVoteResponse nextVoteResponse = (NextVoteResponse) response;

                if (args[2].equalsIgnoreCase("canvote")) {
                    return nextVoteResponse.getNextVote().isBefore(LocalDateTime.now()) ? "yes" : "no";
                } else if (args[2].equalsIgnoreCase("until")) {
                    LocalDateTime time = nextVoteResponse.getNextVote();

                    if (time == null) return "0";

                    long until = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();

                    return DurationFormatUtils.formatDuration(until, plugin.getDurationFormat());
                }
                break;
        }

        return "invalid_params";
    }
}