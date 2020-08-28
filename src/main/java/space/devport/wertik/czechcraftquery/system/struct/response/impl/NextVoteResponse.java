package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

import java.time.LocalDateTime;

@AllArgsConstructor
public class NextVoteResponse extends AbstractResponse {

    @Getter
    private final LocalDateTime nextVote;
    @Getter
    private final String userName;

    @Override
    public String toString() {
        return QueryPlugin.DATE_TIME_FORMAT.format(nextVote) + ":" + userName;
    }
}