package io.rivrs.libbl.event.entity;

import io.rivrs.libbl.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PacketEntityEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final PacketEntity entity;

    public PacketEntityEvent(PacketEntity entity) {
        this.entity = entity;
    }

    public PacketEntityEvent(boolean isAsync, PacketEntity entity) {
        super(isAsync);
        this.entity = entity;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
