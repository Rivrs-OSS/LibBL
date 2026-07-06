package io.rivrs.libbl.model.entities.entity;

import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PacketEvokerFangs extends PacketEntity {

    public PacketEvokerFangs(Location location) {
        super(EntityType.EVOKER_FANGS, location);
    }

    public PacketEvokerFangs(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.EVOKER_FANGS, location);
    }

    public PacketEvokerFangs(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.EVOKER_FANGS, location);
    }
}
