package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class PacketLivingEntity extends PacketEntity {

    private float health = 20.0f;
    private int arrowsInBody = 0;
    private int beeStingerCount;

    public PacketLivingEntity(EntityType type, Location location) {
        super(type, location);
    }

    public PacketLivingEntity(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketLivingEntity(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

    public void health(float health) {
        this.health = health;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void arrowsInBody(int arrowsInBody) {
        this.arrowsInBody = arrowsInBody;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void beeStingerCount(int beeStingerCount) {
        this.beeStingerCount = beeStingerCount;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(9, EntityDataTypes.FLOAT, this.health));
        entityData.add(new EntityData<>(12, EntityDataTypes.INT, this.arrowsInBody));
        entityData.add(new EntityData<>(13, EntityDataTypes.INT, this.beeStingerCount));
        return entityData;
    }


}
