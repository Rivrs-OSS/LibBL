package io.rivrs.libbl.model.entities.entity;

import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PacketWindCharge extends PacketEntity {

    public PacketWindCharge(Location location) {
        super(EntityType.WIND_CHARGE, location);
    }

    public PacketWindCharge(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.WIND_CHARGE, location);
    }

    public PacketWindCharge(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.WIND_CHARGE, location);
    }
}
