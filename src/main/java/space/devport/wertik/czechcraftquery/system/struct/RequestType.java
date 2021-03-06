package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.logging.DebugLevel;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.api.events.ServerAdvanceEvent;
import space.devport.wertik.czechcraftquery.api.events.ServerDropEvent;
import space.devport.wertik.czechcraftquery.api.events.UserCanVoteEvent;
import space.devport.wertik.czechcraftquery.exception.ResponseParserException;
import space.devport.wertik.czechcraftquery.system.struct.context.ContextModifier;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.IResponseParser;
import space.devport.wertik.czechcraftquery.system.struct.response.ResponseListener;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerVotesMonthlyResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerVotesResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.TopVotersResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.UserMonthlyVotesResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.UserVotesResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.TopVote;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.UserVote;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Set;

@Log
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
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context.month(null).user(null);
        }
    }, (cached, toCache) -> {
        if (cached == null) return;

        ServerInfoResponse cachedResponse = (ServerInfoResponse) cached;
        ServerInfoResponse toCacheResponse = (ServerInfoResponse) toCache;

        if (cachedResponse.getPosition() > toCacheResponse.getPosition()) {
            QueryPlugin.callEvent(new ServerAdvanceEvent(toCacheResponse));
        } else if (cachedResponse.getPosition() < toCacheResponse.getPosition()) {
            QueryPlugin.callEvent(new ServerDropEvent(toCacheResponse));
        }
    }),

    NEXT_VOTE("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/next_vote/", input -> {

        TemporalAccessor temporalAccessor = QueryPlugin.DATE_TIME_FORMAT.parse(input.get("next_vote").getAsString());
        LocalDateTime dateTime = LocalDateTime.from(temporalAccessor);

        String userName = input.get("username").getAsString();

        return new NextVoteResponse(userName, dateTime);
    }, new ContextModifier() {
        @Override
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null && context.getUserName() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context.month(null);
        }
    }, (cached, toCache) -> {

        if (cached == null) return;

        NextVoteResponse toCacheResponse = (NextVoteResponse) toCache;
        NextVoteResponse cachedResponse = (NextVoteResponse) cached;

        LocalDateTime now = LocalDateTime.now();

        // If he couldn't vote on the time of last update.
        if (now.isAfter(toCacheResponse.getNextVote()) && cached.getCacheTime().isBefore(cachedResponse.getNextVote())) {
            QueryPlugin.callEvent(new UserCanVoteEvent(toCacheResponse));
        }
    }),

    SERVER_VOTES("https://czech-craft.eu/api/server/%SLUG%/votes/", input -> {

        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new ServerVotesResponse(count, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
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
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null && context.getUserName() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context.month(null);
        }
    }),

    TOP_VOTERS("https://czech-craft.eu/api/server/%SLUG%/voters/", input -> {

        Set<TopVote> topVoters = TopVote.parseMultiple(input.get("data").getAsJsonArray());

        return new TopVotersResponse(topVoters);
    }, new ContextModifier() {
        @Override
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context.user(null).month(null);
        }
    }),

    SERVER_VOTES_MONTHLY("https://czech-craft.eu/api/server/%SLUG%/votes/%MONTH%/", input -> {

        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new ServerVotesMonthlyResponse(count, votes);
    }, new ContextModifier() {

        @Override
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null && context.getMonth() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context.user(null);
        }
    }),

    USER_VOTES_MONTHLY("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/%MONTH%/", input -> {
        int count = input.get("vote_count").getAsInt();
        Set<UserVote> votes = UserVote.parseMultiple(input.get("data").getAsJsonArray());

        return new UserMonthlyVotesResponse(count, votes);
    }, new ContextModifier() {
        @Override
        public boolean verify(@NotNull RequestContext context) {
            return context.getServerSlug() != null && context.getUserName() != null && context.getMonth() != null;
        }

        @Override
        public RequestContext strip(@NotNull RequestContext context) {
            return context;
        }
    });

    public static final RequestType[] VALUES = values();

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

    @Getter
    private final ResponseListener responseListener;

    RequestType(String stringURL, IResponseParser<? extends AbstractResponse> parser, ContextModifier contextModifier) {
        this.stringURL = stringURL;
        this.parser = parser;
        this.contextModifier = contextModifier;
        this.responseListener = (cached, toCache) -> {
        };
    }

    RequestType(String stringURL, IResponseParser<?> parser, ContextModifier contextModifier, ResponseListener responseListener) {
        this.stringURL = stringURL;
        this.parser = parser;
        this.contextModifier = contextModifier;
        this.responseListener = responseListener;
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
        return context != null && contextModifier.verify(context);
    }

    public RequestContext stripContext(final RequestContext context) {
        if (context == null) return null;
        return contextModifier.strip(new RequestContext(context));
    }

    public static void initializeHandlers(QueryPlugin plugin) {
        for (RequestType type : VALUES) {
            RequestHandler handler = new RequestHandler(plugin, type);

            handler.start();

            type.setRequestHandler(handler);
            log.log(DebugLevel.DEBUG, "Registered request handler for " + type.toString());
        }
    }

    /**
     * Force response update in all types.
     */
    public static void updateResponsesForContext(RequestContext context) {
        for (RequestType type : VALUES) {
            type.getRequestHandler().updateResponse(context);
        }
    }

    public static void reloadHandlers() {
        for (RequestType type : VALUES) {
            type.getRequestHandler().start();
            log.log(DebugLevel.DEBUG, String.format("Reloaded request handler update task for %s", type.toString()));
        }
    }

    public static void clearHandlerCaches() {
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
