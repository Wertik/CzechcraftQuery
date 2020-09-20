package space.devport.wertik.czechcraftquery.api.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

public class ServerDropEvent extends Event {

    @Getter
    private final ServerInfoResponse response;

    public ServerDropEvent(ServerInfoResponse response) {
        this.response = response;
    }

    private final static HandlerList handlerList = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}