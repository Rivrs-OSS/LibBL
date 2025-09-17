package io.rivrs.libBL.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libBL.model.entities.entity.living.PacketMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class PacketAgeableMob extends PacketMob {

    @Getter
    private boolean isBaby;

    public PacketAgeableMob(EntityType type, Location location) {
        super(type, location);
    }

    public PacketAgeableMob(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketAgeableMob(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

    public void baby(boolean baby) {
        this.isBaby = baby;

        this.sendPacket(this.buildMetadataPacket());
    }


    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.BOOLEAN, this.isBaby));
        return entityData;
    }
}
