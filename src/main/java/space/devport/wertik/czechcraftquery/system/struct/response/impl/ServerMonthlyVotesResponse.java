package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.UserVote;

import java.util.Set;

@AllArgsConstructor
public class ServerMonthlyVotesResponse extends AbstractResponse {

    @Getter
    private final int count;
    @Getter
    private final Set<UserVote> votes;

    @Override
    public String toString() {
        return count + ";" + votes;
    }
}