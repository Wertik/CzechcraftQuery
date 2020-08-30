package space.devport.wertik.czechcraftquery.exception;

import java.util.concurrent.CompletionException;

public class ResponseParserException extends CompletionException {
    public ResponseParserException(Throwable cause) {
        super(cause);
    }
}