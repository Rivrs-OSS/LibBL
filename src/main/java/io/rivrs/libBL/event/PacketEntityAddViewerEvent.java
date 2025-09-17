package io.rivrs.libBL.event;

import io.rivrs.libBL.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class PacketEntityAddViewerEvent extends PacketEntityEvent implements Cancellable {

    private final Player player;
    private final Reason reason;
    private boolean cancelled;

    public PacketEntityAddViewerEvent(PacketEntity entity, Player player, Reason reason) {
        super(!Bukkit.isPrimaryThread(), entity);
        this.player = player;
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public enum Reason {
        ENTITY_SPAWN,
        PLUGIN
    }
}
