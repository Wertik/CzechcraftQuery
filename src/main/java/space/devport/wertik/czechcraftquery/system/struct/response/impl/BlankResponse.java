package space.devport.wertik.czechcraftquery.system.struct.response.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

@AllArgsConstructor
public class BlankResponse extends AbstractResponse {

    @Getter
    private final String reason;

    @Override
    public String toString() {
        return "No response. Reason: " + reason;
    }
}