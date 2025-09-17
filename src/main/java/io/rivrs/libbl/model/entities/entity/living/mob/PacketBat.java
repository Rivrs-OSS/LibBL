package io.rivrs.libbl.model.entities.entity.living.mob;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.rivrs.libbl.model.entities.entity.living.PacketMob;
import io.rivrs.libbl.model.flag.BatFlags;
import io.rivrs.libbl.model.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketBat extends PacketMob {

    public PacketBat(Location location) {
        super(EntityType.BAT, location);
    }

    public PacketBat(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.BAT, location);
    }

    public PacketBat(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.BAT, location);
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(16, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(BatFlags.class))));
        return entityData;
    }
}
