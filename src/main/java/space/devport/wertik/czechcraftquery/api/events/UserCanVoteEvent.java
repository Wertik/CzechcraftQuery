
package space.devport.wertik.czechcraftquery.api.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.NextVoteResponse;

public class UserCanVoteEvent extends Event {

    private final static HandlerList handlerList = new HandlerList();

    @Getter
    private final NextVoteResponse response;

    public UserCanVoteEvent(NextVoteResponse response) {
        this.response = response;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}