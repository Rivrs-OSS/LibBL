package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libbl.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketItem extends PacketEntity {

    private final ItemStack itemStack;

    public PacketItem(Location location, ItemStack itemStack) {
        super(EntityType.ITEM, location);
        this.itemStack = itemStack;
    }

    public PacketItem(UUID uniqueId, Location location, ItemStack itemStack) {
        super(uniqueId, EntityType.ITEM, location);
        this.itemStack = itemStack;
    }

    public PacketItem(int id, UUID uniqueId, Location location, ItemStack itemStack) {
        super(id, uniqueId, EntityType.ITEM, location);
        this.itemStack = itemStack;
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(this.itemStack)));
        return entityData;
    }
}
