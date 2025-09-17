package io.rivrs.libBL.model.entities.entity.fish;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libBL.model.entities.entity.PacketAbstractFish;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketTropicalFish extends PacketAbstractFish {

    private int variant = 0;

    public PacketTropicalFish(Location location) {
        super(EntityType.TROPICAL_FISH, location);
    }

    public PacketTropicalFish(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.TROPICAL_FISH, location);
    }

    public PacketTropicalFish(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.TROPICAL_FISH, location);
    }

    public void variant(int variant) {
        this.variant = variant;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(17, EntityDataTypes.INT, this.variant));
        return entityData;
    }

}
