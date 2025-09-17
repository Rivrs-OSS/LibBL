package io.rivrs.libBL.model.entities.entity.living;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.util.Vector3f;
import io.rivrs.libBL.model.entities.entity.PacketLivingEntity;
import io.rivrs.libBL.model.flag.ArmorStandFlags;
import io.rivrs.libBL.model.flag.Flag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PacketArmorStand extends PacketLivingEntity {

    private Vector3f headRotation = new Vector3f(0, 0, 0);
    private Vector3f bodyRotation = new Vector3f(0, 0, 0);
    private Vector3f leftArmRotation = new Vector3f(-10, 0, -10);
    private Vector3f rightArmRotation = new Vector3f(-15, 0, 10);
    private Vector3f leftLegRotation = new Vector3f(-1, 0, -1);
    private Vector3f rightLegRotation = new Vector3f(1, 0, 1);

    public PacketArmorStand(Location location) {
        super(EntityType.ARMOR_STAND, location);
    }

    public PacketArmorStand(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.ARMOR_STAND, location);
    }

    public PacketArmorStand(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.ARMOR_STAND, location);
    }

    // Rotations
    public void headRotation(Vector3f rotation) {
        this.headRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void bodyRotation(Vector3f rotation) {
        this.bodyRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void leftArmRotation(Vector3f rotation) {
        this.leftArmRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void rightArmRotation(Vector3f rotation) {
        this.rightArmRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void leftLegRotation(Vector3f rotation) {
        this.leftLegRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    public void rightLegRotation(Vector3f rotation) {
        this.rightLegRotation = rotation;

        this.sendPacket(this.buildMetadataPacket());
    }

    @Override
    public List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = super.entityData(clientVersion);
        entityData.add(new EntityData<>(15, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(ArmorStandFlags.class)))); // Armor stand flags
        entityData.add(new EntityData<>(16, EntityDataTypes.ROTATION, headRotation)); // Head rotation
        entityData.add(new EntityData<>(17, EntityDataTypes.ROTATION, bodyRotation)); // Body rotation
        entityData.add(new EntityData<>(18, EntityDataTypes.ROTATION, leftArmRotation)); // Left arm rotation
        entityData.add(new EntityData<>(19, EntityDataTypes.ROTATION, rightArmRotation)); // Right arm rotation
        entityData.add(new EntityData<>(20, EntityDataTypes.ROTATION, leftLegRotation)); // Left leg rotation
        entityData.add(new EntityData<>(21, EntityDataTypes.ROTATION, rightLegRotation)); // Right leg rotation
        return entityData;
    }
}
