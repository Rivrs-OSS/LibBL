package io.rivrs.libbl.model.entities.entity.living.animal;

import io.rivrs.libbl.model.entities.entity.living.mob.PacketAgeableMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public abstract class PacketAnimal extends PacketAgeableMob {

    public PacketAnimal(EntityType type, Location location) {
        super(type, location);
    }

    public PacketAnimal(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketAnimal(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

}
