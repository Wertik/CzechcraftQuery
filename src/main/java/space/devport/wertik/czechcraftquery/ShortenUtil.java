package space.devport.wertik.czechcraftquery;

import lombok.experimental.UtilityClass;

import java.util.Collection;

@UtilityClass
public class ShortenUtil {
    public String shortenCollection(Collection<?> collection) {
        return "Collection(" + collection.size() + ")[" + collection.getClass().getSimpleName() + "]";
    }

    public String shortenString(String string) {
        return string.length() > 256 ? string.substring(0, 256) + " [...]" : string;
    }
}