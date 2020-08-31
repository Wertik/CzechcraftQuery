package space.devport.wertik.czechcraftquery;

import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class ShortenUtil {
    public String shortenCollection(Collection<?> collection) {
        return "Collection(" + collection.size() + ")[" + collection.getClass().getSimpleName() + "]";
    }

    public String shortenResponse(String rawResponse) {
        return rawResponse.substring(0, Math.min(rawResponse.length(), 256)) + " [...]";
    }
}