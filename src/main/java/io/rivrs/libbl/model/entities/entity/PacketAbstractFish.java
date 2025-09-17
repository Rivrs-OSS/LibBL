package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class PacketAbstractFish extends PacketLivingEntity {

    private boolean fromBucket;

    public PacketAbstractFish(EntityType type, Location location) {
        super(type, location);
    }

    public PacketAbstractFish(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketAbstractFish(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.BOOLEAN, this.fromBucket));
        return entityData;
    }

}
