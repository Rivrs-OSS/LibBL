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

@Getter
public class PacketMagmaCube extends PacketMob {

    private int size = 1;

    public PacketMagmaCube(Location location) {
        super(EntityType.MAGMA_CUBE, location);
    }

    public PacketMagmaCube(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.MAGMA_CUBE, location);
    }

    public PacketMagmaCube(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.MAGMA_CUBE, location);
    }

    public void size(int size) {
        this.size = size;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.INT, this.size));
        return entityData;
    }
}
