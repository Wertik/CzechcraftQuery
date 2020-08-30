package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.TopVote;

import java.util.Set;

@AllArgsConstructor
public class TopVotersResponse extends AbstractResponse {

    @Getter
    private final Set<TopVote> topVoters;

    @Override
    public String toString() {
        return topVoters.toString();
    }
}