package io.rivrs.libbl.model.block;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.protocol.ProtocolManager;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.event.block.FakeBlockAddViewerEvent;
import io.rivrs.libbl.event.block.FakeBlockPlaceEvent;
import io.rivrs.libbl.event.block.FakeBlockRemoveEvent;
import io.rivrs.libbl.event.block.FakeBlockRemoveViewerEvent;
import io.rivrs.libbl.model.ViewerHolder;
import lombok.Getter;
import org.apache.logging.log4j.util.InternalApi;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Getter
public class FakeBlock implements ViewerHolder {

    private final Set<UUID> viewers = new CopyOnWriteArraySet<>();
    private final UUID uniqueID;
    private final BlockData blockData;
    private final int oldStateID;
    private final Location location;
    private final Vector3i position;
    private final String worldName;
    private boolean autoViewable = true;
    private int stateID;
    private boolean placed;

    private final Map<UUID, Integer> sequenceId = new ConcurrentHashMap<>();

    public FakeBlock(BlockData blockData, Location location) {
        this.blockData = blockData;
        Integer stateID = LibBL.get().blockService().getDataState(blockData);
        this.stateID = stateID == null ? 0 : stateID;
        this.oldStateID = stateID;
        if(location.getX() != location.getBlockX() || location.getY() != location.getBlockY() || location.getZ() != location.getBlockZ()) {
            location = new Location(
                    location.getWorld(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );
        }
        this.location = location;
        this.position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.worldName = location.getWorld().getName();

        this.uniqueID = UUID.randomUUID();

        register();
    }


    public void register() {
        LibBL.get().blockService().register(this);
    }

    public void unregister() {
        LibBL.get().blockService().unregister(this);
    }

    public void place() {
        if (this.placed
                || !new FakeBlockPlaceEvent(this).callEvent())
            return;

        // Detect nearby players
        if (this.autoViewable)
            for (Player player : this.location.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(this.location) <= Bukkit.getViewDistance() * 16
                        && new FakeBlockAddViewerEvent(this, player, FakeBlockAddViewerEvent.Reason.BLOCK_PLACE).callEvent())
                    this.viewers.add(player.getUniqueId());
            }

        // Mark as alive
        this.placed = true;
        PacketWrapper<?> packet = this.buildPlacePacket();
        this.viewersAsChannel().forEach(channel -> this.sendPacket(channel, packet));
    }

    /// You should not use this. The only purpose of this is to re-place the fakeBlock in case of the break fake block event.
    @InternalApi
    public void silentPlace(Object channel) {
        this.placed = true;
        PacketWrapper<?> packet = this.buildPlacePacket();
        this.sendPacket(channel, packet);
    }

    public void remove(BlockData blockData) {
        if (!this.placed)
            return;

        new FakeBlockRemoveEvent(this).callEvent();

        List<Player> viewers = this.viewersAsPlayer();
        for (Player viewer : viewers) {
            new FakeBlockRemoveViewerEvent(this, viewer, FakeBlockRemoveViewerEvent.Reason.BLOCK_REMOVED).callEvent();
        }


        PacketWrapper<?> packet = this.buildRemovePacket(blockData);
        this.viewersAsChannel().forEach(channel -> this.sendPacket(channel, packet));

        this.placed = false;
    }

    public void remove() {
        this.remove(Material.AIR.createBlockData());
    }

    /// This methode will attempt to put back what the server know should be at the fakeBlock position.
    ///
    /// The methode is way slower / way laggier than using remove(BlockData), use at your own risks !
    public void smartRemove() {
        if (!location.isWorldLoaded()) {
            remove();
            return;
        }
        World world = location.getWorld();
        CompletableFuture<Chunk> completableFuture = world.getChunkAtAsyncUrgently(location);
        completableFuture.thenAcceptAsync(chunk -> {
            Block block = chunk.getBlock(position.getX(), position.getY(), position.getZ());
            remove(block.getBlockData());
        });
    }

    protected WrapperPlayServerBlockChange buildRemovePacket(BlockData blockData) {
        Integer stateID = LibBL.get().blockService().getDataState(blockData);
        if (stateID == null) {
            stateID = 0;
        }
        this.stateID = stateID;
        return new WrapperPlayServerBlockChange(
                this.position,
                stateID
        );
    }

    protected WrapperPlayServerBlockChange buildPlacePacket() {
        return new WrapperPlayServerBlockChange(
                this.position,
                this.stateID
        );
    }


    @Override
    public void addViewer(Player player) {
        if (this.isViewer(player)
                || !new FakeBlockAddViewerEvent(this, player, FakeBlockAddViewerEvent.Reason.PLUGIN).callEvent())
            return;

        this.viewers.add(player.getUniqueId());
        this.sequenceId.put(player.getUniqueId(), 0);

        PacketWrapper<?> packet = this.buildPlacePacket();
        this.sendPacket(player, packet);
    }

    public void silentAddViewer(Player player) {
        if (this.isViewer(player)
                || !new FakeBlockAddViewerEvent(this, player, FakeBlockAddViewerEvent.Reason.PLUGIN).callEvent())
            return;

        this.viewers.add(player.getUniqueId());
    }

    @Override
    public void removeViewer(Player viewer) {
        if (!this.isViewer(viewer)
                || !new FakeBlockRemoveViewerEvent(this, viewer, FakeBlockRemoveViewerEvent.Reason.PLUGIN).callEvent())
            return;

        this.viewers.remove(viewer.getUniqueId());
        this.sequenceId.remove(viewer.getUniqueId());

        PacketWrapper<?> packet = this.buildPlacePacket();
        this.sendPacket(viewer, packet);

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
    public @Unmodifiable List<Object> viewersAsChannel() {
        return LibBL.get().viewerService().getPlayerChannels(this.viewers);
    }

    @Override
    public List<Player> viewersAsPlayer() {
        return viewers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void autoViewable(boolean autoViewable) {
        this.autoViewable = autoViewable;
    }

    @Override
    public boolean isAutoViewable() {
        return this.autoViewable;
    }

    public void sendPacket(Object channel, PacketWrapper<?>... packetWrappers) {
        if (!placed)
            return;

        if (channel instanceof Player player) {
            channel = LibBL.get().viewerService().getPlayerChannel(player.getUniqueId());
        }

        ProtocolManager protocolManager = PacketEvents.getAPI().getProtocolManager();

        for (PacketWrapper<?> packetWrapper : packetWrappers) {
            protocolManager.sendPacket(channel, packetWrapper);
        }
    }

    public void sendPacket(PacketWrapper<?>... packetWrappers) {
        if (!placed)
            return;

        ProtocolManager protocolManager = PacketEvents.getAPI().getProtocolManager();

        for (PacketWrapper<?> packetWrapper : packetWrappers) {
            for (Object channel : this.viewersAsChannel()) {
                protocolManager.sendPacket(channel, packetWrapper);
            }
        }
    }

    public void setSequenceId(UUID uuid, int sequenceId) {
        this.sequenceId.put(uuid, sequenceId);
    }

    public int getSequenceId(UUID uuid) {
        return this.sequenceId.getOrDefault(uuid, 0);
    }
}
