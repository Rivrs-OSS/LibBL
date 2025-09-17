package io.rivrs.libBL.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libBL.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketFishingBobber extends PacketEntity {

    private int ownerId = 0;
    private int hookedId = 0;
    private boolean catchable;

    public PacketFishingBobber(Location location) {
        super(EntityType.FISHING_BOBBER, location);
    }

    public PacketFishingBobber(UUID uniqueId, Location location, int ownerId) {
        super(uniqueId, EntityType.FISHING_BOBBER, location);
        this.ownerId = ownerId;
    }

    public PacketFishingBobber(int id, UUID uniqueId, Location location, int ownerId) {
        super(id, uniqueId, EntityType.FISHING_BOBBER, location);
        this.ownerId = ownerId;
    }

    public void catchable(boolean catchable) {
        this.catchable = catchable;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void hookedId(int id) {
        this.hookedId = id;

        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    protected WrapperPlayServerSpawnEntity buildSpawnPacket() {
        return new WrapperPlayServerSpawnEntity(
                this.id,
                this.uniqueId,
                SpigotConversionUtil.fromBukkitEntityType(this.type),
                SpigotConversionUtil.fromBukkitLocation(this.location),
                this.location.getYaw(),
                ownerId,
                null
        );
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.INT, this.hookedId));
        entityData.add(new EntityData<>(9, EntityDataTypes.BOOLEAN, this.catchable));
        return entityData;
    }

}
