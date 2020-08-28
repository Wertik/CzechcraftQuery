package space.devport.wertik.czechcraftquery.system.struct;

import lombok.Data;

@Data
public class ContextURL {
    private String serverSlug;
    private String userName;
    // YYYY/MM
    private String month;

    public ContextURL(String serverSlug) {
        this.serverSlug = serverSlug;
    }

    public ContextURL(String serverSlug, String userName) {
        this.serverSlug = serverSlug;
        this.userName = userName;
    }

    public ContextURL(String serverSlug, String userName, String month) {
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
}
