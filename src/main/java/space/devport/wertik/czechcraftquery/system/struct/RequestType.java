package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.api.events.CzechcraftServerAdvanceEvent;
import space.devport.wertik.czechcraftquery.exception.ResponseParserException;
import space.devport.wertik.czechcraftquery.system.struct.context.ContextModifier;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.IResponseParser;
import space.devport.wertik.czechcraftquery.system.struct.response.ResponseListener;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.*;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.TopVote;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.UserVote;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Set;

public enum RequestType {

    SERVER_INFO("https://czech-craft.eu/api/server/%SLUG%/", input -> {

        String serverSlug = input.get("slug").getAsString();
        String serverName = input.get("name").getAsString();
        String address = input.get("address").getAsString();

        int position = input.get("position").getAsInt();
        int votes = input.get("votes").getAsInt();

        return new ServerInfoResponse(serverSlug, serverName, address, position, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.month(null).user(null);
        }
    }),

    NEXT_VOTE("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/next_vote/", input -> {

        TemporalAccessor temporalAccessor = QueryPlugin.DATE_TIME_FORMAT.parse(input.get("next_vote").getAsString());
        LocalDateTime dateTime = LocalDateTime.from(temporalAccessor);

        String userName = input.get("username").getAsString();

        return new NextVoteResponse(userName, dateTime);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null && context.getUserName() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.month(null);
        }
    }),

    SERVER_VOTES("https://czech-craft.eu/api/server/%SLUG%/votes/", input -> {

        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new ServerVotesResponse(count, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.month(null).user(null);
        }
    }),

    USER_VOTES("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/", input -> {

        LocalDateTime dateTime = LocalDateTime.from(QueryPlugin.DATE_TIME_FORMAT.parse(input.get("next_vote").getAsString()));
        String username = input.get("username").getAsString();

        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new UserVotesResponse(dateTime, username, count, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null && context.getUserName() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.month(null);
        }
    }),

    TOP_VOTERS("https://czech-craft.eu/api/server/%SLUG%/voters/", input -> {

        Set<TopVote> topVoters = TopVote.parseMultiple(input.get("data").getAsJsonArray());

        return new TopVotersResponse(topVoters);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.user(null).month(null);
        }
    }),

    SERVER_VOTES_MONTHLY("https://czech-craft.eu/api/server/%SLUG%/votes/%MONTH%/", input -> {

        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new ServerVotesMonthlyResponse(count, votes);
    }, new ContextModifier() {

        @Override
        public boolean verify(RequestContext context) {
            return context.getServerSlug() != null && context.getMonth() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context.user(null);
        }
    }, (ResponseListener<ServerInfoResponse>) (cached, toCache) -> {
        if (cached == null) return;

        if (cached.getPosition() < toCache.getPosition()) {
            Bukkit.getPluginManager().callEvent(new CzechcraftServerAdvanceEvent(toCache));
        }
    }),

    // Seems to be removed from the API.
    @Deprecated
    USER_VOTES_MONTHLY("https://czech-craft.eu/api/server/%SLUG%/players/%USER%/%MONTH%/", input -> {
        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new UserMonthlyVotesResponse(count, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(RequestContext context) {
            return false;// context.getServerSlug() != null && context.getUserName() != null && context.getMonth() != null;
        }

        @Override
        public RequestContext strip(RequestContext context) {
            return context;
        }
    });

    @Getter
    private final String stringURL;

    @Getter
    private final IResponseParser<?> parser;

    //TODO Replace interface with required fields.
    @Getter
    private final ContextModifier contextModifier;

    @Getter
    @Setter
    private RequestHandler requestHandler;

    RequestType(String stringURL, IResponseParser<?> parser, ContextModifier contextModifier, ResponseListener<? extends AbstractResponse>... listener) {
        this.stringURL = stringURL;
        this.parser = parser;
        this.contextModifier = contextModifier;
    }

    /**
     * IResponseParser#parse wrapper to handle exceptions.
     */
    public AbstractResponse parse(JsonObject input) throws ResponseParserException {
        try {
            return this.parser.parse(input);
        } catch (Exception e) {
            throw new ResponseParserException(e);
        }
    }

    public boolean verifyContext(RequestContext context) {
        return contextModifier.verify(context);
    }

    public RequestContext stripContext(RequestContext context) {
        return contextModifier.strip(context);
    }

    public static void initializeHandlers(QueryPlugin plugin) {
        for (RequestType type : values()) {
            RequestHandler handler = new RequestHandler(plugin, type);

            handler.loadOptions();
            handler.start();

            type.setRequestHandler(handler);
            plugin.getConsoleOutput().debug("Registered request handler for " + type.toString());
        }
    }

    /**
     * Force response update in all types.
     */
    public static void updateResponsesForContext(RequestContext context) {
        for (RequestType type : values()) {
            type.getRequestHandler().updateResponse(context);
        }
    }

    public static void reloadHandlers(QueryPlugin plugin) {
        for (RequestType type : values()) {
            type.getRequestHandler().stop();
            type.getRequestHandler().loadOptions();
            type.getRequestHandler().start();
            plugin.getConsoleOutput().debug("Reloaded request handler update task for " + type.toString());
        }
    }

    public static void clearHandlerCaches(QueryPlugin plugin) {
        for (RequestType type : values()) {
            type.getRequestHandler().clearCache();
        }
    }

    public static RequestType fromString(String str) {
        for (RequestType type : values()) {
            if (type.toString().equalsIgnoreCase(str) ||
                    type.toString().replace("_", "").equalsIgnoreCase(str) ||
                    type.toString().replaceAll("_", "-").equalsIgnoreCase(str))
                return type;
        }
        return null;
    }
}
