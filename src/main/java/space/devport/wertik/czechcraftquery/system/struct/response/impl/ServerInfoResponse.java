package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

public class ServerInfoResponse extends AbstractResponse {

    @Getter
    private final String serverName;
    @Getter
    private final int votes;

    public ServerInfoResponse(String serverName, int votes) {
        this.serverName = serverName;
        this.votes = votes;
    }
}