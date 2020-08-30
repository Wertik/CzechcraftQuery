package space.devport.wertik.czechcraftquery.system.struct.response.impl.struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.QueryPlugin;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class UserVote {

    @Getter
    private final LocalDateTime dateTime;
    @Getter
    private final String username;
    @Getter
    private final boolean delivered;

    public static UserVote parse(JsonObject jsonObject) {

        LocalDateTime dateTime = LocalDateTime.from(QueryPlugin.DATE_TIME_FORMAT.parse(jsonObject.get("datetime").getAsString()));
        String username = jsonObject.get("username").getAsString();
        boolean delivered = jsonObject.get("delivered").getAsBoolean();

        return new UserVote(dateTime, username, delivered);
    }

    public static Set<UserVote> parseMultiple(JsonArray jsonArray) {
        Set<UserVote> votes = new HashSet<>();
        for (JsonElement jsonElement : jsonArray) {
            if (!jsonElement.isJsonObject()) continue;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            votes.add(parse(jsonObject));
        }
        return votes;
    }

    @Override
    public String toString() {
        return username + ";" + QueryPlugin.DATE_TIME_FORMAT.format(dateTime) + ";" + delivered;
    }
}