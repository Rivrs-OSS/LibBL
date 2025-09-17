package io.rivrs.libBL.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3i;
import io.rivrs.libBL.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketFallingBlock extends PacketEntity {

    public PacketFallingBlock(Location location) {
        super(EntityType.FALLING_BLOCK, location);
    }

    public PacketFallingBlock(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.FALLING_BLOCK, location);
    }

    public PacketFallingBlock(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.FALLING_BLOCK, location);
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<Vector3i>(8, EntityDataTypes.BLOCK_POSITION, new Vector3i(this.location.blockX(), this.location.blockY(), this.location.blockZ())));
        return entityData;
    }
}
