package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketInteraction extends PacketEntity {

    private float width = 1.f;
    private float height = 1.f;
    private boolean responsive = false;

    public PacketInteraction(Location location) {
        super(EntityType.INTERACTION, location);
    }

    public PacketInteraction(Location location, float width, float height, boolean responsive) {
        super(EntityType.INTERACTION, location);
        this.width = width;
        this.height = height;
        this.responsive = responsive;
        this.sendPacket(this.buildMetadataPacket());
    }

    public PacketInteraction(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.INTERACTION, location);
    }

    public PacketInteraction(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.INTERACTION, location);
    }

    public void width(float width) {
        this.width = width;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void height(float height) {
        this.height = height;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void responsive(boolean responsive) {
        this.responsive = responsive;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.FLOAT, this.width));
        entityData.add(new EntityData<>(9, EntityDataTypes.FLOAT, this.height));
        entityData.add(new EntityData<>(10, EntityDataTypes.BOOLEAN, this.responsive));
        return entityData;
    }
}
