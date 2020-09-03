package space.devport.wertik.czechcraftquery.system.struct.response;

import org.jetbrains.annotations.Nullable;

public interface ResponseListener {
    void listen(@Nullable AbstractResponse cached, AbstractResponse toCache);
}