package io.rivrs.libBL.model.entities.entity.fish;

import io.rivrs.libBL.model.entities.entity.PacketAbstractFish;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PacketCod extends PacketAbstractFish {

    public PacketCod(Location location) {
        super(EntityType.COD, location);
    }

    public PacketCod(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.COD, location);
    }

    public PacketCod(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.COD, location);
    }
}
