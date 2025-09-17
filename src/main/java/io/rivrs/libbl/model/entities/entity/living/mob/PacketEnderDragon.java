package io.rivrs.libbl.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.entity.living.PacketMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketEnderDragon extends PacketMob {

    private EnderDragon.Phase phase = EnderDragon.Phase.CIRCLING;

    public PacketEnderDragon(Location location) {
        super(EntityType.ENDER_DRAGON, location);
    }

    public PacketEnderDragon(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.ENDER_DRAGON, location);
    }

    public PacketEnderDragon(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.ENDER_DRAGON, location);
    }

    public void phase(EnderDragon.Phase phase) {
        this.phase = phase;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.INT, this.phase.ordinal()));
        return entityData;
    }
}
