package space.devport.wertik.czechcraftquery.system.struct;

import lombok.Getter;
import lombok.Setter;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.context.ContextVerifier;
import space.devport.wertik.czechcraftquery.system.struct.context.RequestContext;
import space.devport.wertik.czechcraftquery.system.struct.response.IResponseParser;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

public enum RequestType {

    SERVER_INFO("https://czech-craft.eu/api/server/%SLUG%/", input -> {

        String serverSlug = input.get("slug").getAsString();
        String serverName = input.get("name").getAsString();
        String address = input.get("address").getAsString();

        int position = input.get("position").getAsInt();
        int votes = input.get("votes").getAsInt();

        return new ServerInfoResponse(serverSlug, serverName, address, position, votes);
    }, context -> context.getServerSlug() != null),

    NEXT_VOTE("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/next_vote/", input -> {
        TemporalAccessor temporalAccessor = QueryPlugin.DATE_TIME_FORMAT.parse(input.get("next_vote").getAsString());
        LocalDateTime dateTime = LocalDateTime.from(temporalAccessor);

        String userName = input.get("username").getAsString();

        return new NextVoteResponse(dateTime, userName);
    }, context -> context.getServerSlug() != null && context.getUserName() != null);

    @Getter
    private final String stringURL;

    @Getter
    private final IResponseParser<?> parser;

    @Getter
    private final ContextVerifier contextVerifier;

    @Getter
    @Setter
    private RequestHandler<?> requestHandler;

    RequestType(String stringURL, IResponseParser<?> parser, ContextVerifier contextVerifier) {
        this.stringURL = stringURL;
        this.parser = parser;
        this.contextVerifier = contextVerifier;
    }

    public boolean verifyContext(RequestContext context) {
        return contextVerifier.apply(context);
    }

    public static void initializeHandlers(QueryPlugin plugin) {
        for (RequestType type : values()) {
            RequestHandler<?> handler = new RequestHandler<>(plugin, type);

            handler.loadOptions();
            handler.start();

            type.setRequestHandler(handler);
            plugin.getConsoleOutput().debug("Registered request handler for " + type.toString());
        }
    }

    /**
     * Force response update in all types.
     */
    public static void updateResponses(RequestContext context) {
        for (RequestType type : values()) {
            type.getRequestHandler().sendRequest(context);
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
