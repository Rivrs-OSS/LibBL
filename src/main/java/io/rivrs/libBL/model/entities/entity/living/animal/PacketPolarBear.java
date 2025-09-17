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
public class PacketPolarBear extends PacketAnimal {

    private boolean standingUp;

    public PacketPolarBear(Location location) {
        super(EntityType.POLAR_BEAR, location);
    }

    public PacketPolarBear(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.POLAR_BEAR, location);
    }

    public PacketPolarBear(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.POLAR_BEAR, location);
    }

    public void standingUp(boolean boost) {
        this.standingUp = standingUp;

        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(17, EntityDataTypes.BOOLEAN, this.standingUp));
        return entityData;
    }
}
