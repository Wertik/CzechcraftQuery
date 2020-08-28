package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

public interface IParser<T extends AbstractResponse> {
    T parse(JsonObject input);
}