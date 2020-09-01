package space.devport.wertik.czechcraftquery.system.struct.response;

import org.jetbrains.annotations.Nullable;

public interface ResponseListener<T extends AbstractResponse> {
    void listen(@Nullable T cached, T toCache);
}