package io.rivrs.libbl.model.entities;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityMetadataProvider;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.event.PacketEntityAddViewerEvent;
import io.rivrs.libbl.event.PacketEntityDespawnEvent;
import io.rivrs.libbl.event.PacketEntityRemoveViewerEvent;
import io.rivrs.libbl.event.PacketEntitySpawnEvent;
import io.rivrs.libbl.model.EquipmentType;
import io.rivrs.libbl.model.ViewerHolder;
import io.rivrs.libbl.model.flag.EntityFlags;
import io.rivrs.libbl.model.flag.Flag;
import io.rivrs.libbl.utils.ParticleUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public abstract class PacketEntity implements EntityMetadataProvider, ViewerHolder {

    // Identification
    protected final int id;
    protected final UUID uniqueId;
    protected final EntityType type;
    protected final ItemStack[] equipment = new ItemStack[6];
    protected final Set<UUID> viewers = new CopyOnWriteArraySet<>();
    // Passengers
    protected final Set<PacketEntity> passengersIds = new CopyOnWriteArraySet<>();
    // Velocity
    protected Vector3d velocity;
    // Metadata
    private final Set<Flag> flags = new HashSet<>();
    // Data
    protected Location location;
    protected int airTicks = 300;
    protected Component customName = Component.empty();
    protected boolean customNameVisible = false;
    protected boolean silent;
    protected boolean gravity = true;
    protected Pose pose = Pose.STANDING;
    protected int frozenTicks = 0;
    // Viewers
    protected boolean autoViewable = true;
    // State
    protected boolean alive;

    public PacketEntity(EntityType type, Location location) {
        this(SpigotReflectionUtil.generateEntityId(), UUID.randomUUID(), type, location);
    }

    public PacketEntity(UUID uniqueId, EntityType type, Location location) {
        this(SpigotReflectionUtil.generateEntityId(), uniqueId, type, location);
    }

    public PacketEntity(int id, UUID uniqueId, EntityType type, Location location) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.type = type;
        this.location = location;

        this.register();
    }

    public void spawn() {
        if (this.alive
                || !new PacketEntitySpawnEvent(this).callEvent())
            return;

        // Detect nearby players
        if (this.autoViewable)
            for (Player player : this.location.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(this.location) <= LibBL.ENTITY_SIMULATION_DISTANCE_SQR()
                        && new PacketEntityAddViewerEvent(this, player, PacketEntityAddViewerEvent.Reason.ENTITY_SPAWN).callEvent())
                    this.viewers.add(player.getUniqueId());
            }

        // Mark as alive
        this.alive = true;

        // Send packets
        for (UUID viewerId : this.viewers) {
            Player viewer = Bukkit.getPlayer(viewerId);
            if (viewer == null)
                continue;

            LinkedList<PacketWrapper<?>> packets = this.buildSpawnPackets(viewer);
            for (PacketWrapper<?> packet : packets) {
                this.sendPacket(viewer, packet);
            }
        }
    }

    protected LinkedList<PacketWrapper<?>> buildSpawnPackets(Player player) {
        LinkedList<PacketWrapper<?>> packets = new LinkedList<>();

        packets.add(this.buildSpawnPacket());
        packets.add(this.buildMetadataPacket());
        if (this.hasEquipment())
            packets.add(this.buildEquipmentPacket());
        // Passengers packet has to be sent after the entity spawn packet, otherwise it won't work
        Bukkit.getScheduler().runTaskAsynchronously(LibBL.get(), () -> {
            if (this.hasPassengers()){
                this.sendPacket(player, this.buildSetPassengersPacket());
            }
        });
        if(velocity != null)
            packets.add(this.buildVelocityPacket());
        return packets;
    }

    public void teleport(Location location) {
        this.sendPacket(this.buildTeleportPacket(location));
        this.location = location.clone();
    }

    public void teleport(Player player, Location location) {
        this.sendPacket(player, this.buildTeleportPacket(location));
    }

    public void teleport(Player player, Location location, Location lastLocation) {
        this.sendPacket(player, this.buildTeleportPacket(location, lastLocation));
    }

    public void rotate(float yaw, float pitch) {
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);

        this.sendPacket(this.buildRotationPacket());
    }

    public void rotate(Player player, float yaw, float pitch) {
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);

        this.sendPacket(player, this.buildRotationPacket());
    }

    public void despawn() {
        if (!this.alive)
            return;

        new PacketEntityDespawnEvent(this).callEvent();

        List<Player> viewers = this.viewersAsPlayers();
        for (Player viewer : viewers) {
            new PacketEntityRemoveViewerEvent(this, viewer, PacketEntityRemoveViewerEvent.Reason.ENTITY_REMOVED).callEvent();
        }

        LinkedList<PacketWrapper<?>> packets = this.buildDestroyPackets();
        for (PacketWrapper<?> packet : packets) {
            for (Player viewer : viewers) {
                this.sendPacket(viewer, packet);
            }
        }

        this.alive = false;
    }

    protected LinkedList<PacketWrapper<?>> buildDestroyPackets() {
        LinkedList<PacketWrapper<?>> packets = new LinkedList<>();

        packets.add(this.buildDestroyPacket());

        return packets;
    }

    public void updateAttributes(Player player, List<WrapperPlayServerUpdateAttributes.Property> properties) {
        this.sendPacket(player, new WrapperPlayServerUpdateAttributes(
                this.id,
                properties
        ));
    }

    public void updateAttributes(List<WrapperPlayServerUpdateAttributes.Property> properties) {
        this.sendPacket(new WrapperPlayServerUpdateAttributes(
                this.id,
                properties
        ));
    }

    public void customName(Component customName) {
        this.customName = customName;
        this.updateMetadata();
    }

    public void customNameVisible(boolean visible) {
        this.customNameVisible = visible;
        this.updateMetadata();
    }

    public void silent(boolean silent) {
        this.silent = silent;
        this.updateMetadata();
    }

    public void gravity(boolean gravity) {
        this.gravity = gravity;
        this.updateMetadata();
    }

    public void pose(Pose pose) {
        this.pose = pose;
        this.updateMetadata();
    }

    public void frozenTicks(int ticks) {
        this.frozenTicks = ticks;
        this.updateMetadata();
    }

    public void airTicks(int ticks) {
        this.airTicks = ticks;
        this.updateMetadata();
    }

    public void register() {
        LibBL.get().entities().register(this);
    }

    public void unregister() {
        LibBL.get().entities().unregister(this);
    }

    // Particles
    public void particle(Vector vector, Particle particle) {
        sendPacket(ParticleUtils.create(location.clone().add(vector), particle));
    }

    public void particle(Vector vector, Location location, com.github.retrooper.packetevents.protocol.particle.Particle<?> particle) {
        sendPacket(ParticleUtils.create(location.clone().add(vector), particle));
    }

    // Flags
    public void addFlag(Flag flag) {
        this.flags.add(flag);

        this.updateMetadata();
    }

    public void addFlags(Flag... flags) {
        this.flags.addAll(Arrays.asList(flags));

        this.updateMetadata();
    }

    public void removeFlag(Flag flag) {
        this.flags.remove(flag);

        this.updateMetadata();
    }

    public boolean hasFlag(Flag flag) {
        return this.flags.contains(flag);
    }

    @Unmodifiable
    public Set<Flag> flags() {
        return Set.copyOf(this.flags);
    }

    @SuppressWarnings("unchecked")
    @Unmodifiable
    protected <T extends Flag> T[] flags(Class<T> type) {
        return this.flags.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toArray(size -> (T[]) Array.newInstance(type, size));
    }

    // Equipment
    public ItemStack equiment(EquipmentType type) {
        ItemStack itemStack = this.equipment[type.ordinal()];
        return itemStack != null ? itemStack : ItemStack.empty();
    }

    public boolean hasEquipment() {
        for (ItemStack item : this.equipment) {
            if (item != null)
                return true;
        }

        return false;
    }

    public boolean hasPassengers() {
        return !this.passengersIds.isEmpty();
    }

    public boolean hasEquipment(EquipmentType type) {
        return this.equipment[type.ordinal()] != null;
    }

    public void equip(EquipmentType type, ItemStack item) {
        this.equipment[type.ordinal()] = item;

        if (hasEquipment())
            sendPacket(buildEquipmentPacket());
    }

    public void addPassenger(PacketEntity entity) {
        if (this.passengersIds.add(entity))
            sendPacket(buildSetPassengersPacket());
    }

    public void removePassenger(PacketEntity entity) {
        if (this.passengersIds.remove(entity))
            sendPacket(buildSetPassengersPacket());
    }

    public void addVelocity(Vector3d velocity) {
        this.velocity = velocity;
        if(alive){
            sendPacket(buildVelocityPacket());
        }
    }

    public void rotateHead(Player player, float yaw) {
        this.location.setYaw(yaw);
        this.sendPacket(player, new WrapperPlayServerEntityHeadLook(
                this.id,
                yaw
        ));
    }

    public void rotateHead(float yaw) {
        this.location.setYaw(yaw);
        this.sendPacket(new WrapperPlayServerEntityHeadLook(
                this.id,
                yaw
        ));
    }

    // Metadata
    @Override
    public @NotNull List<EntityData<?>> entityData(@NotNull ClientVersion clientVersion) {
        List<EntityData<?>> entityData = new ArrayList<>();

        entityData.add(new EntityData<>(0, EntityDataTypes.BYTE, Flag.toBitMask(this.flags(EntityFlags.class)))); // Entity flags
        entityData.add(new EntityData<>(1, EntityDataTypes.INT, this.airTicks)); // Air ticks

        // Custom name
        if (customName != null && !customName.equals(Component.empty()))
            entityData.add(new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(customName))); // Custom name
        entityData.add(new EntityData<>(3, EntityDataTypes.BOOLEAN, customNameVisible)); // Custom name visible

        entityData.add(new EntityData<>(4, EntityDataTypes.BOOLEAN, silent)); // Silent
        entityData.add(new EntityData<>(5, EntityDataTypes.BOOLEAN, !gravity)); // No gravity
        entityData.add(new EntityData<>(6, EntityDataTypes.ENTITY_POSE, SpigotConversionUtil.fromBukkitPose(this.pose))); // Pose
        entityData.add(new EntityData<>(7, EntityDataTypes.INT, this.frozenTicks)); // Frozen ticks

        return entityData;
    }

    // Viewers
    @Override
    public void addViewer(Player player) {
        if (this.isViewer(player)
                || !new PacketEntityAddViewerEvent(this, player, PacketEntityAddViewerEvent.Reason.PLUGIN).callEvent())
            return;

        this.viewers.add(player.getUniqueId());

        LinkedList<PacketWrapper<?>> packets = this.buildSpawnPackets(player);
        for (PacketWrapper<?> packet : packets) {
            this.sendPacket(player, packet);
        }
    }

    @Override
    public void removeViewer(Player viewer) {
        if (!this.isViewer(viewer)
                || !new PacketEntityRemoveViewerEvent(this, viewer, PacketEntityRemoveViewerEvent.Reason.PLUGIN).callEvent())
            return;

        this.viewers.remove(viewer.getUniqueId());

        LinkedList<PacketWrapper<?>> packets = this.buildDestroyPackets();
        for (PacketWrapper<?> packet : packets) {
            this.sendPacket(viewer, packet);
        }
    }

    @Override
    public boolean isViewer(Player player) {
        return this.viewers.contains(player.getUniqueId());
    }

    @Override
    public @Unmodifiable Set<UUID> viewers() {
        return Set.copyOf(this.viewers);
    }

    @Override
    public void autoViewable(boolean autoViewable) {
        this.autoViewable = autoViewable;
    }

    @Override
    public boolean isAutoViewable() {
        return this.autoViewable;
    }

    // Packets

    protected WrapperPlayServerSpawnEntity buildSpawnPacket() {
        return new WrapperPlayServerSpawnEntity(
                this.id,
                this.uniqueId,
                SpigotConversionUtil.fromBukkitEntityType(this.type),
                SpigotConversionUtil.fromBukkitLocation(this.location),
                this.location.getYaw(),
                0,
                null
        );
    }

    protected WrapperPlayServerDestroyEntities buildDestroyPacket() {
        return new WrapperPlayServerDestroyEntities(this.id);
    }

    protected PacketWrapper<?> buildTeleportPacket(Location newLocation) {
        return this.buildTeleportPacket(newLocation, this.location);
    }

    protected PacketWrapper<?> buildTeleportPacket(Location newLocation, Location lastLocation) {
        if (newLocation.x() == lastLocation.x()
                && newLocation.y() == lastLocation.y()
                && newLocation.z() == lastLocation.z()
                && (newLocation.getYaw() != lastLocation.getYaw()
                || newLocation.getPitch() != lastLocation.getPitch())) {
            return buildRotationPacket();
        }

        // Relative move packet
        if (newLocation.getWorld().equals(lastLocation.getWorld())
                && newLocation.distance(lastLocation) < 8) {
            return new WrapperPlayServerEntityRelativeMoveAndRotation(
                    this.id,
                    newLocation.getX() - lastLocation.getX(),
                    newLocation.getY() - lastLocation.getY(),
                    newLocation.getZ() - lastLocation.getZ(),
                    newLocation.getYaw(),
                    newLocation.getPitch(),
                    true
            );
        }

        // Teleport packet
        return new WrapperPlayServerEntityTeleport(
                this.id,
                SpigotConversionUtil.fromBukkitLocation(newLocation),
                true
        );
    }

    protected WrapperPlayServerEntityRotation buildRotationPacket() {
        return new WrapperPlayServerEntityRotation(
                this.id,
                this.location.getYaw(),
                this.location.getPitch(),
                true
        );
    }

    protected WrapperPlayServerEntityEquipment buildEquipmentPacket() {
        // Build equipment list
        List<Equipment> equipments = new ArrayList<>();
        for (EquipmentType type : EquipmentType.values()) {
            ItemStack equipment = this.equipment[type.ordinal()];
            if (equipment != null && !equipment.isEmpty())
                equipments.add(new Equipment(type.slot(), SpigotConversionUtil.fromBukkitItemStack(equipment)));
        }

        return new WrapperPlayServerEntityEquipment(this.id, equipments);
    }

    protected PacketWrapper<WrapperPlayServerEntityMetadata> buildMetadataPacket() {
        return this.buildMetadataPacket(this);
    }

    protected PacketWrapper<WrapperPlayServerEntityMetadata> buildMetadataPacket(EntityMetadataProvider provider) {
        return new WrapperPlayServerEntityMetadata(
                this.id,
                provider
        );
    }

    protected PacketWrapper<WrapperPlayServerSetPassengers> buildSetPassengersPacket() {
        return new WrapperPlayServerSetPassengers(
                this.id,
                this.passengersIds.stream().mapToInt(i -> i.id).toArray()
        );
    }

    protected PacketWrapper<WrapperPlayServerEntityVelocity> buildVelocityPacket() {
        if(this.velocity == null)
            return null;
        Vector3d buffer = this.velocity;
        this.velocity = null;
        return new WrapperPlayServerEntityVelocity(
                this.id,
                buffer
        );
    }


    public void updateMetadata() {
        this.sendPacket(this.buildMetadataPacket());
    }

    public void updateMetadata(Player player) {
        this.sendPacket(player, this.buildMetadataPacket());
    }

    public void updateMetadata(Player player, EntityMetadataProvider provider) {
        this.sendPacket(player, this.buildMetadataPacket(provider));
    }

    public void updateMetadata(EntityMetadataProvider provider) {
        this.sendPacket(this.buildMetadataPacket(provider));
    }

    public void sendPacket(PacketWrapper<?>... packetWrappers) {
        if (!alive)
            return;
        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

        for (PacketWrapper<?> packetWrapper : packetWrappers) {
            for (Player viewer : this.viewersAsPlayers()) {
                playerManager.sendPacket(viewer, packetWrapper);
            }
        }
    }

    public void sendPacket(Player player, PacketWrapper<?>... packetWrappers) {
        if (!alive)
            return;

        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        for (PacketWrapper<?> packetWrapper : packetWrappers) {
            playerManager.sendPacket(player, packetWrapper);
        }
    }

    @Override
    public List<Player> viewersAsPlayers() {
        return this.viewers.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }
}
