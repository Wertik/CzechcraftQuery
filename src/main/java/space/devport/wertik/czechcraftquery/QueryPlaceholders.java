package space.devport.wertik.czechcraftquery;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.czechcraftquery.system.struct.RequestType;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.*;

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

        if (args.length < 3)
            return "not_enough_args";

        RequestType type = RequestType.fromString(args[0]);

        if (type == null) return "invalid_type";

        RequestContext context = new RequestContext(args[1]);

        if (player != null)
            context.setUserName(player.getName());

        AbstractResponse response = type.getRequestHandler().getResponse(context).join();

        if (!response.isValid())
            return "no_response";

        switch (type) {

            case SERVER_INFO:
                /*
                 * _name%
                 * _votes%
                 * _position%
                 * _address%
                 * */

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

            case SERVER_VOTES:
                /*
                 * _count%
                 * */

                ServerVotesResponse serverVotesResponse = (ServerVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    return String.valueOf(serverVotesResponse.getCount());
                }
                break;

            case TOP_VOTERS:
                /*
                 * _position% -- player position, only handles top 25
                 * _<position>%
                 * */

                TopVotersResponse topVotersResponse = (TopVotersResponse) response;

                int position = parseInt(args[2]);

                if (position > 0)
                    return String.valueOf(topVotersResponse.getTopVoters().get(Math.max(0, position - 1)).getVotes());

                if (args[2].equalsIgnoreCase("position")) {
                    if (player == null) return "no_player";
                    return String.valueOf(topVotersResponse.findPosition(player.getUniqueId()));
                } else return "-1";

            case USER_VOTES:
                /*
                 * _nextvote%
                 * _count%
                 * */

                UserVotesResponse userVotesResponse = (UserVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    return String.valueOf(userVotesResponse.getCount());
                } else if (args[2].equalsIgnoreCase("nextvote")) {
                    return QueryPlugin.DATE_TIME_FORMAT.format(userVotesResponse.getNextVote());
                }
                break;

            case NEXT_VOTE:
                //TODO Add formatted & different time elements
                /*
                 * _canvote%
                 * _until%
                 * */

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
            case SERVER_VOTES_MONTHLY:
                /*
                 * _count%
                 * */

                ServerMonthlyVotesResponse serverMonthlyVotesResponse = (ServerMonthlyVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    return String.valueOf(serverMonthlyVotesResponse.getCount());
                }
                break;

            case USER_VOTES_MONTHLY:
                /*
                 * _count%
                 * */

                if (player == null) return "no_player";

                UserMonthlyVotesResponse userMonthlyVotesResponse = (UserMonthlyVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    return String.valueOf(userMonthlyVotesResponse.getCount());
                }
        }

        return "invalid_params";
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}