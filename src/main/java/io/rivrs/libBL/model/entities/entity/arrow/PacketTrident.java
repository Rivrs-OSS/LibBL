package io.rivrs.libBL.model.entities.entity.arrow;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libBL.model.entities.entity.PacketAbstractArrow;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketTrident extends PacketAbstractArrow {

    private int loyaltyLevel = 0;
    private boolean enchanted = false;

    public PacketTrident(Location location) {
        super(EntityType.TRIDENT, location);
    }

    public PacketTrident(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.TRIDENT, location);
    }

    public PacketTrident(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.TRIDENT, location);
    }

    public void loyaltyLevel(int loyaltyLevel) {
        if (loyaltyLevel < 0 || loyaltyLevel > 127)
            throw new IllegalArgumentException("Loyalty level must be between 0 and 127");

        this.loyaltyLevel = loyaltyLevel;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void enchanted(boolean enchanted) {
        this.enchanted = enchanted;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(11, EntityDataTypes.BYTE, (byte) this.loyaltyLevel));
        entityData.add(new EntityData<>(12, EntityDataTypes.BOOLEAN, this.enchanted));
        return entityData;
    }


}
