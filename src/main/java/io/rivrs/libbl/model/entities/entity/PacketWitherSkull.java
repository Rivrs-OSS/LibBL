package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketWitherSkull extends PacketEntity {

    /** When true the skull renders "charged" (blue), like the wither's dangerous skulls. */
    private boolean charged;

    public PacketWitherSkull(Location location) {
        super(EntityType.WITHER_SKULL, location);
    }

    public PacketWitherSkull(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.WITHER_SKULL, location);
    }

    public PacketWitherSkull(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.WITHER_SKULL, location);
    }

    public void charged(boolean charged) {
        this.charged = charged;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.BOOLEAN, this.charged));
        return entityData;
    }
}
