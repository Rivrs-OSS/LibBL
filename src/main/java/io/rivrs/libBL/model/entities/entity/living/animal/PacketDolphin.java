package io.rivrs.libBL.model.entities.entity.living.animal;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketDolphin extends PacketAnimal {

    private boolean hasFish;
    private int moistureLevel = 2400;

    public PacketDolphin(Location location) {
        super(EntityType.DOLPHIN, location);
    }

    public PacketDolphin(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.DOLPHIN, location);
    }

    public PacketDolphin(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.DOLPHIN, location);
    }

    public void setHasFish(boolean hasFish) {
        this.hasFish = hasFish;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void setMoistureLevel(int moistureLevel) {
        this.moistureLevel = moistureLevel;

        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(17, EntityDataTypes.BOOLEAN, this.hasFish));
        entityData.add(new EntityData<>(18, EntityDataTypes.INT, this.moistureLevel));
        return entityData;
    }
}
