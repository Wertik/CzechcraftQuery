package space.devport.wertik.czechcraftquery.system.struct;

import lombok.Getter;
import lombok.Setter;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

public enum RequestType {

    SERVER_INFO("https://czech-craft.eu/api/server/%SLUG%/", input -> {
        int votes = input.get("votes").getAsInt();
        String serverName = input.get("name").getAsString();
        return new ServerInfoResponse(serverName, votes);
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
            type.setRequestHandler(new RequestHandler<>(plugin, type));
        }
    }
}
