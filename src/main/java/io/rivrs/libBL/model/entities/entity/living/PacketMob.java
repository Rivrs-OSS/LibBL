package io.rivrs.libBL.model.entities.entity.living;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libBL.model.entities.entity.PacketLivingEntity;
import io.rivrs.libBL.model.flag.Flag;
import io.rivrs.libBL.model.flag.MobFlags;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class PacketMob extends PacketLivingEntity {

    public PacketMob(EntityType type, Location location) {
        super(type, location);
    }

    public PacketMob(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketMob(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(15, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(MobFlags.class))));
        return entityData;
    }
}
