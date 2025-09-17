package io.rivrs.libbl.model;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EquipmentType {
    HELMET(EquipmentSlot.HELMET, org.bukkit.inventory.EquipmentSlot.HEAD),
    CHEST_PLATE(EquipmentSlot.CHEST_PLATE, org.bukkit.inventory.EquipmentSlot.CHEST),
    LEGGINGS(EquipmentSlot.LEGGINGS, org.bukkit.inventory.EquipmentSlot.LEGS),
    BOOTS(EquipmentSlot.BOOTS, org.bukkit.inventory.EquipmentSlot.FEET),
    MAIN_HAND(EquipmentSlot.MAIN_HAND, org.bukkit.inventory.EquipmentSlot.HAND),
    OFF_HAND(EquipmentSlot.OFF_HAND, org.bukkit.inventory.EquipmentSlot.OFF_HAND);

    private final EquipmentSlot slot;
    private final org.bukkit.inventory.EquipmentSlot bukkitSlot;
}
