package space.devport.wertik.czechcraftquery.system.struct.response.impl.struct;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
public class TopVote {

    @Getter
    private final String username;
    @Getter
    private final int votes;

    public static TopVote parse(JsonObject jsonObject) {
        String username = jsonObject.get("username").getAsString();
        int votes = jsonObject.get("vote_count").getAsInt();
        return new TopVote(username, votes);
    }

    public static Set<TopVote> parseMultiple(JsonArray jsonArray) {
        Set<TopVote> topVoters = new LinkedHashSet<>();
        for (JsonElement jsonElement : jsonArray) {
            if (!jsonElement.isJsonObject()) continue;
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            topVoters.add(parse(jsonObject));
        }
        return topVoters;
    }

    @Override
    public String toString() {
        return username + ";" + votes;
    }
}