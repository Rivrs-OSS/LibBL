package io.rivrs.libbl.model.entities.entity;

import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PacketDragonFireball extends PacketEntity {

    public PacketDragonFireball(Location location) {
        super(EntityType.DRAGON_FIREBALL, location);
    }

    public PacketDragonFireball(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.DRAGON_FIREBALL, location);
    }

    public PacketDragonFireball(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.DRAGON_FIREBALL, location);
    }
}
