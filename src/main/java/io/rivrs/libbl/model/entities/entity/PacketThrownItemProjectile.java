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
public abstract class PacketThrownItemProjectile extends PacketEntity {

    private ItemStack item;

    public PacketThrownItemProjectile(EntityType type, Location location, ItemStack item) {
        super(type, location);
        this.item = item;
    }

    public PacketThrownItemProjectile(UUID uniqueId, EntityType type, Location location, ItemStack item) {
        super(uniqueId, type, location);
        this.item = item;
    }

    public PacketThrownItemProjectile(int id, UUID uniqueId, EntityType type, Location location, ItemStack item) {
        super(id, uniqueId, type, location);
        this.item = item;
    }

    public void item(ItemStack item) {
        this.item = item;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(this.item)));
        return entityData;
    }
}
