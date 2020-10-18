package space.devport.wertik.czechcraftquery.system.struct.response;

import lombok.Getter;

import java.time.LocalDateTime;

public abstract class AbstractResponse {

    @Getter
    private final LocalDateTime cacheTime;

    public AbstractResponse() {
        this.cacheTime = LocalDateTime.now();
    }

    /**
     * Return whether or not is the response a valid type.
     * Used to distinguish between BlankResponse and the valid responses.
     */
    public boolean isValid() {
        return true;
    }
}