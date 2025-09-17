package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.model.flag.ArrowFlags;
import io.rivrs.libbl.model.flag.Flag;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class PacketAbstractArrow extends PacketEntity {

    private int piercingLevel;
    private boolean inGround;

    public PacketAbstractArrow(EntityType type, Location location) {
        super(type, location);
    }

    public PacketAbstractArrow(UUID uniqueId, EntityType type, Location location) {
        super(uniqueId, type, location);
    }

    public PacketAbstractArrow(int id, UUID uniqueId, EntityType type, Location location) {
        super(id, uniqueId, type, location);
    }

    public void piercingLevel(int piercingLevel) {
        if (piercingLevel < 0 || piercingLevel > 127)
            throw new IllegalArgumentException("Piercing level must be between 0 and 127");

        this.piercingLevel = piercingLevel;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void inGround(boolean inGround) {
        this.inGround = inGround;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(ArrowFlags.class))));
        entityData.add(new EntityData<>(9, EntityDataTypes.BYTE, (byte) this.piercingLevel));
        entityData.add(new EntityData<>(10, EntityDataTypes.BOOLEAN, this.inGround));
        return entityData;
    }
}
