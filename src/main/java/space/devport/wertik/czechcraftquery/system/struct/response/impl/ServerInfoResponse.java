package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

@AllArgsConstructor
public class ServerInfoResponse extends AbstractResponse {

    @Getter
    private final String serverSlug;
    @Getter
    private final String serverName;
    @Getter
    private final String address;
    @Getter
    private final int position;
    @Getter
    private final int votes;

    @Override
    public String toString() {
        return serverSlug + ";" + serverName + ";" + address + ";" + position + ";" + votes;
    }
}