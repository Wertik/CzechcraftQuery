package space.devport.wertik.czechcraftquery.system.struct.response;

import com.google.gson.JsonObject;

public interface IResponseParser<T extends AbstractResponse> {
    T parse(JsonObject input);
}