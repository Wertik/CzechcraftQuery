package space.devport.wertik.czechcraftquery.system.struct.context;

import lombok.Getter;

import java.util.Objects;

public class RequestContext {

    @Getter
    private final String serverSlug;

    @Getter
    private String userName;

    // yyyy/MM
    @Getter
    private String month;

    public RequestContext(RequestContext context) {
        this.serverSlug = context.getServerSlug();
        this.userName = context.getUserName();
        this.month = context.getMonth();
    }

    public RequestContext(String serverSlug) {
        this.serverSlug = serverSlug;
    }

    public RequestContext(String serverSlug, String userName) {
        this.serverSlug = serverSlug;
        this.userName = userName;
    }

    public RequestContext(String serverSlug, String userName, String month) {
        this.serverSlug = serverSlug;
        this.userName = userName;
        this.month = month;
    }

    public RequestContext user(String userName) {
        this.userName = userName;
        return this;
    }

    public RequestContext month(String month) {
        this.month = month;
        return this;
    }

    public String parse(String str) {
        str = parse(str, "%SLUG%", serverSlug);
        str = parse(str, "%USER%", userName);
        str = parse(str, "%MONTH%", month);
        return str;
    }

    private String parse(String str, String key, Object value) {
        if (value == null) return str;
        return str.replaceAll("(?i)" + key, value.toString());
    }

    @Override
    public String toString() {
        return serverSlug + (userName == null ? "" : ";" + userName) + (month == null ? "" : ";" + month);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestContext context = (RequestContext) o;
        return Objects.equals(serverSlug, context.serverSlug) &&
                Objects.equals(userName, context.userName) &&
                Objects.equals(month, context.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverSlug, userName, month);
    }
}
