package io.rivrs.libBL.model.entities.entity.arrow;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libBL.model.entities.entity.PacketAbstractArrow;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketArrow extends PacketAbstractArrow {

    private Color color = null;

    public PacketArrow(Location location) {
        super(EntityType.ARROW, location);
    }

    public PacketArrow(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.ARROW, location);
    }

    public PacketArrow(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.ARROW, location);
    }

    public void color(Color color) {
        this.color = color;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(11, EntityDataTypes.INT, this.color == null ? -1 : this.color.asRGB()));
        return entityData;
    }

}
