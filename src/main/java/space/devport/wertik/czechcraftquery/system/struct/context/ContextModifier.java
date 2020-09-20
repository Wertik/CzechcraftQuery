package space.devport.wertik.czechcraftquery.system.struct.context;

import org.jetbrains.annotations.NotNull;

public interface ContextModifier {

    boolean verify(@NotNull RequestContext context);

    RequestContext strip(@NotNull RequestContext context);
}