package space.devport.wertik.czechcraftquery.system.struct;

import lombok.Getter;
import lombok.Setter;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

import java.time.LocalDateTime;

public enum RequestType {

    SERVER_INFO("https://czech-craft.eu/api/server/%SLUG%/", input -> {
        String serverSlug = input.get("slug").getAsString();
        String serverName = input.get("name").getAsString();
        String address = input.get("address").getAsString();
        int position = input.get("position").getAsInt();
        int votes = input.get("votes").getAsInt();
        return new ServerInfoResponse(serverSlug, serverName, address, position, votes);
    }),

    NEXT_VOTE("https://czech-craft.eu/api/server/%SLUG%/player/%USER%/next_vote/", input -> {
        LocalDateTime dateTime = LocalDateTime.from(QueryPlugin.DATE_TIME_FORMAT.parse(input.get("next_vote").getAsString()));
        String userName = input.get("username").getAsString();
        return new NextVoteResponse(dateTime, userName);
    });

    @Getter
    private final String stringURL;

    @Getter
    private final IParser<?> parser;

    @Getter
    @Setter
    private RequestHandler<?> requestHandler;

    RequestType(String stringURL, IParser<?> parser) {
        this.stringURL = stringURL;
        this.parser = parser;
    }

    public static void initializeHandlers(QueryPlugin plugin) {
        for (RequestType type : values()) {
            RequestHandler<?> handler = new RequestHandler<>(plugin, type);

            handler.loadOptions();
            handler.start();

            type.setRequestHandler(handler);
        }
    }
}
