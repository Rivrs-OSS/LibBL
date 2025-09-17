package io.rivrs.libBL.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import io.rivrs.libBL.model.entities.entity.living.PacketMob;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class PacketShulker extends PacketMob {

    private float scale = 1;
    //private Direction rotation = Rotation.NONE;
    private byte shieldHeight = 0;
    private byte color = 16; // Default color (16 is the default for shulkers)

    public PacketShulker(Location location) {
        super(EntityType.SHULKER, location);
    }

    public PacketShulker(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.SHULKER, location);
    }

    public PacketShulker(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.SHULKER, location);
    }

    public void scale(float scale) {
        this.scale = scale;
        this.updateAttributes(List.of(new WrapperPlayServerUpdateAttributes.Property(
                Attributes.SCALE,
                scale,
                new ArrayList<>()
        )));
        this.sendPacket(this.buildMetadataPacket());
    }

    public void shieldHeight(byte shieldHeight) {
        this.shieldHeight = shieldHeight;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void color(byte color) {
        this.color = color;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        return super.entityData(clientVersion);
    }
}
