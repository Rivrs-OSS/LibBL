package io.rivrs.libbl.model.entities.entity.fish;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.entity.PacketAbstractFish;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketSalmon extends PacketAbstractFish {

    private Size size = Size.MEDIUM;

    public PacketSalmon(Location location) {
        super(EntityType.SALMON, location);
    }

    public PacketSalmon(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.SALMON, location);
    }

    public PacketSalmon(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.SALMON, location);
    }

    public PacketSalmon(UUID uniqueId, Location location, Size size) {
        super(uniqueId, EntityType.SALMON, location);
        this.size = size;
    }

    public PacketSalmon(int id, UUID uniqueId, Location location, Size size) {
        super(id, uniqueId, EntityType.SALMON, location);
        this.size = size;
    }

    public PacketSalmon(Location location, Size size) {
        super(EntityType.SALMON, location);
        this.size = size;
    }

    public void size(Size type) {
        this.size = type;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(17, EntityDataTypes.INT, this.size.ordinal()));
        return entityData;
    }

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE
    }

}
