package io.rivrs.libbl.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ViewerHolder {

    void addViewer(Player player);

    void removeViewer(Player viewer);

    boolean isViewer(Player player);

    @Unmodifiable
    Set<UUID> viewers();

    @Unmodifiable
    List<Player> viewersAsPlayers();

    void autoViewable(boolean autoViewable);

    boolean isAutoViewable();
}
