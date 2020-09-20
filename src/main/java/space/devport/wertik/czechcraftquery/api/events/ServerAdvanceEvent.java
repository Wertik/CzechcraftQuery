package space.devport.wertik.czechcraftquery.api.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.ServerInfoResponse;

public class ServerAdvanceEvent extends Event {

    @Getter
    private final ServerInfoResponse response;

    public ServerAdvanceEvent(ServerInfoResponse response) {
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