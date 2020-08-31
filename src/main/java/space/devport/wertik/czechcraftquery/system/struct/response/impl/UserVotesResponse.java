package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.ShortenUtil;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.UserVote;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@AllArgsConstructor
public class UserVotesResponse extends AbstractResponse {

    @Getter
    private final LocalDateTime nextVote;
    @Getter
    private final String username;
    @Getter
    private final int count;
    private final Set<UserVote> votes;

    public Set<UserVote> getVotes() {
        return Collections.unmodifiableSet(votes);
    }

    @Override
    public String toString() {
        return username + ";" + QueryPlugin.DATE_TIME_FORMAT.format(nextVote) + ";" + count + ";" + ShortenUtil.shortenCollection(votes);
    }
}