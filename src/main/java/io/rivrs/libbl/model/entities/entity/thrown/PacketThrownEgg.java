package io.rivrs.libbl.model.entities.entity.thrown;

import io.rivrs.libbl.model.entities.entity.PacketThrownItemProjectile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class PacketThrownEgg extends PacketThrownItemProjectile {

    public PacketThrownEgg(Location location, @Nullable ItemStack item) {
        super(EntityType.EGG, location, (item == null || item.isEmpty()) ? new ItemStack(Material.EGG) : item);
    }

    public PacketThrownEgg(UUID uniqueId, Location location, @Nullable ItemStack item) {
        super(uniqueId, EntityType.EGG, location, (item == null || item.isEmpty()) ? new ItemStack(Material.EGG) : item);
    }

    public PacketThrownEgg(int id, UUID uniqueId, Location location, @Nullable ItemStack item) {
        super(id, uniqueId, EntityType.EGG, location, (item == null || item.isEmpty()) ? new ItemStack(Material.EGG) : item);
    }

}
