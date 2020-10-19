package space.devport.wertik.czechcraftquery.system.struct.response.impl.struct;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class VoteData {

    @Getter
    private final String username;

    private OfflinePlayer offlinePlayer;

    public VoteData(String username) {
        this.username = username;
    }

    @Nullable
    public UUID getUniqueID() {
        return getOfflinePlayer() == null ? null : getOfflinePlayer().getUniqueId();
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public OfflinePlayer getOfflinePlayer() {
        if (offlinePlayer == null) this.offlinePlayer = Bukkit.getOfflinePlayer(username);
        return this.offlinePlayer;
    }

    @Nullable
    public Player getPlayer() {
        return getOfflinePlayer() != null && getOfflinePlayer().isOnline() ? getOfflinePlayer().getPlayer() : null;
    }
}