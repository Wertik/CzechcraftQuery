package space.devport.wertik.czechcraftquery.system.struct.response;

public abstract class AbstractResponse {

    /**
     * Return whether or not is the response a valid type.
     * Used to distinguish between BlankResponse and the valid responses.
     */
    public boolean isValid() {
        return true;
    }
}