package space.devport.wertik.czechcraftquery.system.struct;

import com.google.gson.JsonObject;
import space.devport.wertik.czechcraftquery.QueryPlugin;
import space.devport.wertik.czechcraftquery.system.struct.response.AbstractResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler<T extends AbstractResponse> implements Runnable {

    private final QueryPlugin plugin;

    private final RequestType requestType;

    private int refreshInterval;

    private final Map<String, T> cache = new HashMap<>();

    public RequestHandler(QueryPlugin plugin, RequestType requestType) {
        this.plugin = plugin;
        this.requestType = requestType;
    }

    public void loadOptions() {
        this.refreshInterval = plugin.getConfig().getInt("refresh-rates." + requestType.toString().toLowerCase(), 20);
    }

    public T sendRequest(ContextURL context) {
        JsonObject response = plugin.getService().sendRequest(requestType, context);
        T t = (T) requestType.getParser().parse(response);
        this.cache.put(context.parse(requestType.getStringURL()), t);
        return t;
    }

    @Override
    public void run() {

    }
}