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
public class PacketPhantom extends PacketMob {

    private int size = 0;

    public PacketPhantom(Location location) {
        super(EntityType.PHANTOM, location);
    }

    public PacketPhantom(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.PHANTOM, location);
    }

    public PacketPhantom(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.PHANTOM, location);
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
