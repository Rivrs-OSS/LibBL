package io.rivrs.libbl.model.entities.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libbl.model.entities.entity.PacketDisplay;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketBlockDisplay extends PacketDisplay {

    private final BlockData blockData;

    public PacketBlockDisplay(Location location, BlockData blockData) {
        super(EntityType.BLOCK_DISPLAY, location);
        this.blockData = blockData;
    }

    public PacketBlockDisplay(UUID uniqueId, Location location, BlockData blockData) {
        super(uniqueId, EntityType.BLOCK_DISPLAY, location);
        this.blockData = blockData;
    }

    public PacketBlockDisplay(int id, UUID uniqueId, Location location, BlockData blockData) {
        super(id, uniqueId, EntityType.BLOCK_DISPLAY, location);
        this.blockData = blockData;
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(23, EntityDataTypes.BLOCK_STATE, SpigotConversionUtil.fromBukkitBlockData(blockData).getGlobalId()));
        return entityData;
    }


}
