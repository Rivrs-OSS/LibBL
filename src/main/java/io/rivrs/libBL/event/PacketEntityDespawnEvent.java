package io.rivrs.libBL.event;

import io.rivrs.libBL.model.entities.PacketEntity;
import org.bukkit.Bukkit;

public class PacketEntityDespawnEvent extends PacketEntityEvent {

    public PacketEntityDespawnEvent(PacketEntity entity) {
        super(!Bukkit.isPrimaryThread(), entity);
    }

}
