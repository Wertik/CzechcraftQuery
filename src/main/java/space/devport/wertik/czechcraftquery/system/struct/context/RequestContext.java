package space.devport.wertik.czechcraftquery.system.struct.context;

import lombok.Data;

import java.util.Objects;

@Data
public class RequestContext {

    private String serverSlug;
    private String userName;

    // yyyy/MM
    private String month;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestContext that = (RequestContext) o;
        return Objects.equals(serverSlug, that.serverSlug) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(month, that.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverSlug, userName, month);
    }
}