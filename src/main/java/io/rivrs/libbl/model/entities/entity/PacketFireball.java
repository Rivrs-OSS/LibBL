package io.rivrs.libbl.model.entities.entity;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libbl.model.entities.PacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketFireball extends PacketEntity {

    /**
     * The item the fireball renders as; {@code null} leaves the vanilla fire-charge texture.
     */
    private ItemStack item;

    public PacketFireball(Location location) {
        super(EntityType.FIREBALL, location);
    }

    public PacketFireball(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.FIREBALL, location);
    }

    public PacketFireball(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.FIREBALL, location);
    }

    public void item(ItemStack item) {
        this.item = item;
        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        if (this.item != null) {
            entityData.add(new EntityData<>(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(this.item)));
        }
        return entityData;
    }
}
