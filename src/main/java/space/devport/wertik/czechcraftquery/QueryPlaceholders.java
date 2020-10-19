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
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerVotesMonthlyResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerVotesResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.TopVotersResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.UserMonthlyVotesResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.UserVotesResponse;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

        if (type == null)
            return "invalid_type";

        RequestContext context = new RequestContext(args[1]);

        if (player != null)
            context.user(player.getName());

        for (String arg : args) {
            try {
                Date date = QueryPlugin.MONTH_FORMAT.parse(arg);
                if (date != null) {
                    context.month(arg);
                    break;
                }
            } catch (ParseException ignored) {
            }
        }

        AbstractResponse response = type.getRequestHandler().getResponse(context).join();

        if (!response.isValid())
            return "no_response";

        String result = "invalid_params";
        switch (type) {

            default:
                result = "invalid_params";
                break;

            case SERVER_INFO:
                /*
                 * _name%
                 * _votes%
                 * _position%
                 * _address%
                 * */

                ServerInfoResponse serverInfoResponse = (ServerInfoResponse) response;

                if (args[2].equalsIgnoreCase("name")) {
                    result = serverInfoResponse.getServerName();
                } else if (args[2].equalsIgnoreCase("address")) {
                    result = serverInfoResponse.getAddress();
                } else if (args[2].equalsIgnoreCase("position")) {
                    result = String.valueOf(serverInfoResponse.getPosition());
                } else if (args[2].equalsIgnoreCase("votes")) {
                    result = String.valueOf(serverInfoResponse.getVotes());
                }
                break;

            case SERVER_VOTES:
                /*
                 * _count%
                 * */

                ServerVotesResponse serverVotesResponse = (ServerVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    result = String.valueOf(serverVotesResponse.getCount());
                }
                break;

            case TOP_VOTERS:
                /*
                 * _position% -- player position, only handles top 25
                 * _<position>_votes%
                 * _<position>_name%
                 * */

                TopVotersResponse topVotersResponse = (TopVotersResponse) response;

                int position = parseInt(args[2]);

                if (position > 0) {
                    if (args.length < 4) {
                        result = "not_enough_args";
                        break;
                    }

                    if (args[3].equalsIgnoreCase("votes")) {
                        result = String.valueOf(topVotersResponse.getTopVoters().get(Math.max(0, position - 1)).getVotes());
                    } else {
                        result = topVotersResponse.getTopVoters().get(position - 1).getUsername();
                    }
                    break;
                }

                if (args[2].equalsIgnoreCase("position")) {
                    if (player == null) {
                        result = "no_player";
                        break;
                    }

                    result = String.valueOf(topVotersResponse.findPosition(player.getUniqueId()));
                } else result = "-1";
                break;

            case USER_VOTES:
                /*
                 * _nextvote%
                 * _count%
                 * */

                UserVotesResponse userVotesResponse = (UserVotesResponse) response;

                if (args[2].equalsIgnoreCase("count")) {
                    result = String.valueOf(userVotesResponse.getCount());
                } else if (args[2].equalsIgnoreCase("nextvote")) {
                    result = QueryPlugin.DATE_TIME_FORMAT.format(userVotesResponse.getNextVote());
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
                    result = nextVoteResponse.getNextVote().isBefore(LocalDateTime.now()) ? "yes" : "no";
                } else if (args[2].equalsIgnoreCase("until")) {
                    LocalDateTime time = nextVoteResponse.getNextVote();

                    if (time == null) {
                        result = "0";
                        break;
                    }

                    long until = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();

                    result = DurationFormatUtils.formatDuration(until, plugin.getDurationFormat());
                }
                break;

            case SERVER_VOTES_MONTHLY:
                /*
                 * <month>_count%
                 * */

                if (args.length < 4) {
                    result = "not_enough_args";
                    break;
                }

                if (context.getMonth() == null) {
                    result = "no_month";
                    break;
                }

                ServerVotesMonthlyResponse serverVotesMonthlyResponse = (ServerVotesMonthlyResponse) response;

                if (args[3].equalsIgnoreCase("count")) {
                    result = String.valueOf(serverVotesMonthlyResponse.getCount());
                }
                break;

            case USER_VOTES_MONTHLY:
                /*
                 * <month>_count%
                 * */

                if (player == null) {
                    result = "no_player";
                    break;
                }

                if (args.length < 4) {
                    result = "not_enough_args";
                    break;
                }

                if (context.getMonth() == null) {
                    result = "no_month";
                    break;
                }

                UserMonthlyVotesResponse userMonthlyVotesResponse = (UserMonthlyVotesResponse) response;

                if (args[3].equalsIgnoreCase("count")) {
                    result = String.valueOf(userMonthlyVotesResponse.getCount());
                }
                break;
        }

        return result;
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}