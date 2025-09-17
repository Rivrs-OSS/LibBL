package io.rivrs.libbl.model.entities.entity.arrow;

import io.rivrs.libbl.model.entities.entity.PacketAbstractArrow;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PacketSpectralArrow extends PacketAbstractArrow {

    public PacketSpectralArrow(Location location) {
        super(EntityType.SPECTRAL_ARROW, location);
    }

    public PacketSpectralArrow(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.SPECTRAL_ARROW, location);
    }

    public PacketSpectralArrow(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.SPECTRAL_ARROW, location);
    }
}
