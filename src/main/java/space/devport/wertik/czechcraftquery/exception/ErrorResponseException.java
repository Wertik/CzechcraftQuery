package space.devport.wertik.czechcraftquery.exception;

import lombok.Getter;

import java.util.concurrent.CompletionException;

public class ErrorResponseException extends CompletionException {

    @Getter
    private final String responseText;

    public ErrorResponseException(String text) {
        super("Error response from host, text: " + text);
        this.responseText = text;
    }
}