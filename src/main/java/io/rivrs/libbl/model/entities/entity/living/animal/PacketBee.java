package io.rivrs.libbl.model.entities.entity.living.animal;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.flag.BeeFlags;
import io.rivrs.libbl.model.flag.Flag;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketBee extends PacketAnimal {

    private int angerTime = 0;

    public PacketBee(Location location) {
        super(EntityType.BEE, location);
    }

    public PacketBee(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.BEE, location);
    }

    public PacketBee(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.BEE, location);
    }

    public void angerTime(int angerTime) {
        this.angerTime = angerTime;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(17, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(BeeFlags.class))));
        entityData.add(new EntityData<>(18, EntityDataTypes.INT, this.angerTime));
        return entityData;
    }

}
