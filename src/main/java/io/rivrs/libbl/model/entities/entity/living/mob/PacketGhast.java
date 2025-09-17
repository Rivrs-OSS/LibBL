package io.rivrs.libbl.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.entity.living.PacketMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketGhast extends PacketMob {

    private boolean attacking;

    public PacketGhast(Location location) {
        super(EntityType.GHAST, location);
    }

    public PacketGhast(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.GHAST, location);
    }

    public PacketGhast(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.GHAST, location);
    }

    public void attacking(boolean attacking) {
        this.attacking = attacking;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.BOOLEAN, this.attacking));
        return entityData;
    }
}
