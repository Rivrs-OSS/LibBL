package io.rivrs.libBL.event;

import io.rivrs.libBL.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class PacketEntityRemoveViewerEvent extends PacketEntityEvent {

    private final Player player;
    private final Reason reason;

    public PacketEntityRemoveViewerEvent(PacketEntity entity, Player player, Reason reason) {
        super(!Bukkit.isPrimaryThread(), entity);
        this.player = player;
        this.reason = reason;
    }

    public enum Reason {
        ENTITY_REMOVED,
        PLUGIN
    }
}
