package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;
import space.devport.wertik.czechcraftquery.system.struct.response.impl.struct.TopVote;

import java.util.*;
import java.util.stream.Collectors;

public class TopVotersResponse extends AbstractResponse {

    private final List<TopVote> topVoters;

    public TopVotersResponse(Set<TopVote> topVoters) {
        this.topVoters = topVoters.stream()
                .sorted(Comparator.comparingInt(TopVote::getVotes).reversed())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public int findPosition(UUID uniqueID) {
        int n = 1;
        for (TopVote topVote : getTopVoters()) {
            UUID topUUID = topVote.getUniqueID();
            if (topUUID != null && topUUID.equals(uniqueID))
                return n;
            n++;
        }
        return -1;
    }

    public List<TopVote> getTopVoters() {
        return Collections.unmodifiableList(topVoters);
    }

    @Override
    public String toString() {
        return topVoters.toString();
    }
}