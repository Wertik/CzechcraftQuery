package space.devport.wertik.czechcraftquery.system.struct.response.impl.invalid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

@AllArgsConstructor
public class BlankResponse extends AbstractResponse {

    @Getter
    private final String reason;

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return "No response. Reason: " + reason;
    }
}