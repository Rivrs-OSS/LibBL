package io.rivrs.libbl.event;

import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

public class PacketEntitySpawnEvent extends PacketEntityEvent implements Cancellable {

    private boolean cancelled;

    public PacketEntitySpawnEvent(PacketEntity entity) {
        super(!Bukkit.isPrimaryThread(), entity);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
