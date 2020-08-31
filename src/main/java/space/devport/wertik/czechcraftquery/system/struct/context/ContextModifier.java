package space.devport.wertik.czechcraftquery.system.struct.context;

public interface ContextModifier {

    boolean verify(RequestContext context);

    RequestContext strip(RequestContext context);
}