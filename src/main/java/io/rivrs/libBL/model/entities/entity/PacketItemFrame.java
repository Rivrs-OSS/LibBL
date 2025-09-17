package io.rivrs.libBL.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libBL.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketItemFrame extends PacketEntity {

    private ItemStack itemStack = ItemStack.empty();
    private Rotation rotation = Rotation.NONE; // TODO ReImplement Rotation properly
    // TODO impl orientation

    public PacketItemFrame(Location location) {
        super(EntityType.ITEM_FRAME, location);
    }

    public PacketItemFrame(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.ITEM_FRAME, location);
    }

    public PacketItemFrame(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.ITEM_FRAME, location);
    }

    public void itemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.sendPacket(this.buildMetadataPacket());
    }

    public void rotation(Rotation rotation) {
        this.rotation = rotation;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(this.itemStack)));
        entityData.add(new EntityData<>(9, EntityDataTypes.INT, this.rotation.ordinal()));
        return entityData;
    }

}
