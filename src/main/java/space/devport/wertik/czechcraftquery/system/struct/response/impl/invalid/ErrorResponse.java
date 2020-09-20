package space.devport.wertik.czechcraftquery.system.struct.response.impl.invalid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

/**
 * Returned when the API response is not OK.
 */
@RequiredArgsConstructor
public class ErrorResponse extends AbstractResponse {

    @Getter
    private final String response;

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return "Error response from API. Text: " + this.response;
    }
}