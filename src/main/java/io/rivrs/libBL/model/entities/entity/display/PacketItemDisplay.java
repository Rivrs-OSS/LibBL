package io.rivrs.libBL.model.entities.entity.display;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libBL.model.entities.entity.PacketDisplay;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
public class PacketItemDisplay extends PacketDisplay {

    private final ItemStack itemStack;
    private ItemDisplay.ItemDisplayTransform displayTransform = ItemDisplay.ItemDisplayTransform.NONE;

    public PacketItemDisplay(Location location, ItemStack itemStack) {
        super(EntityType.ITEM_DISPLAY, location);
        this.itemStack = itemStack;
    }

    public PacketItemDisplay(UUID uniqueId, Location location, ItemStack itemStack) {
        super(uniqueId, EntityType.ITEM_DISPLAY, location);
        this.itemStack = itemStack;
    }

    public PacketItemDisplay(int id, UUID uniqueId, Location location, ItemStack itemStack) {
        super(id, uniqueId, EntityType.ITEM_DISPLAY, location);
        this.itemStack = itemStack;
    }

    public void displayTransform(ItemDisplay.ItemDisplayTransform displayTransform) {
        this.displayTransform = displayTransform;

        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(23, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(this.itemStack)));
        entityData.add(new EntityData<>(24, EntityDataTypes.BYTE, (byte) this.displayTransform.ordinal()));
        return entityData;
    }
}
