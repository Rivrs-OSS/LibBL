package io.rivrs.libbl.event;

import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Bukkit;

public class PacketEntityDespawnEvent extends PacketEntityEvent {

    public PacketEntityDespawnEvent(PacketEntity entity) {
        super(!Bukkit.isPrimaryThread(), entity);
    }

}
