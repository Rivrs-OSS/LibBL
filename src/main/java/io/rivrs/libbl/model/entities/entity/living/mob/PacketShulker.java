package io.rivrs.libbl.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.entity.living.PacketMob;
import io.rivrs.libbl.utils.Direction;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketShulker extends PacketMob {

    private Direction direction = Direction.DOWN;
    private byte shieldHeight = 0;
    private byte color = 16; // Check at https://minecraft.wiki/w/Dye#Color_values to know the color value (16 is default fallback)

    public PacketShulker(Location location) {
        super(EntityType.SHULKER, location);
    }

    public PacketShulker(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.SHULKER, location);
    }

    public PacketShulker(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.SHULKER, location);
    }

    public void shieldHeight(byte shieldHeight) {
        this.shieldHeight = shieldHeight;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void color(byte color) {
        this.color = color;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void direction(Direction direction) {
        this.direction = direction;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.INT, this.direction.value()));
        entityData.add(new EntityData<>(17, EntityDataTypes.BYTE, this.shieldHeight));
        entityData.add(new EntityData<>(18, EntityDataTypes.BYTE, this.color));
        return entityData;
    }
}
