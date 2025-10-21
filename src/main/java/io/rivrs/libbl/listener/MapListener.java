package io.rivrs.libbl.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAcknowledgeBlockChanges;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.event.block.FakeBlockBreakEvent;
import io.rivrs.libbl.event.block.FakeBlockInteractEvent;
import io.rivrs.libbl.model.block.FakeBlock;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

import java.util.List;

@RequiredArgsConstructor
public class MapListener implements PacketListener {

    private final LibBL plugin;

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Server.CHUNK_DATA)) {

            WrapperPlayServerChunkData packet = new WrapperPlayServerChunkData(event);
            Player player = event.getPlayer();
            World world = player.getWorld();
            if (!plugin.blockService().existsAtWorld(world))
                return;
            List<FakeBlock> blocks = plugin.blockService().findByChunk(packet.getColumn().getX(), packet.getColumn().getZ(), world);

            Column column = packet.getColumn();

            BaseChunk[] baseChunks = column.getChunks();
            for (int sectionY = 0; sectionY < baseChunks.length; sectionY++) {
                BaseChunk chunkSection = baseChunks[sectionY];
                if (chunkSection == null)
                    continue; // Skip if this section doesn't exist
                // Iterate over the blocks that need replacement
                for (FakeBlock information : blocks) {

                    if (!information.placed())
                        continue;
                    if (!information.isViewer(player) && !information.autoViewable())
                        continue;

                    if (information.isAutoViewable() && !information.isViewer(player)) {
                        information.addViewer(player);
                    }

                    // Get world coordinates of the block
                    int worldBlockX = information.location().getBlockX();
                    int worldBlockY = information.location().getBlockY() + 64;
                    int worldBlockZ = information.location().getBlockZ();

                    // Ensure the block's Y-coordinate is within the current chunk section
                    if (worldBlockY < sectionY * 16 || worldBlockY >= (sectionY + 1) * 16) {
                        continue; // Block isn't in this chunk section, skip
                    }
                    // Convert world coordinates to chunk-relative coordinates
                    int relativeX = worldBlockX & 15; // X within the chunk (0-15)
                    int relativeY = worldBlockY % 16; // Y within this chunk section (0-15)
                    int relativeZ = worldBlockZ & 15; // Z within the chunk (0-15)

                    // Set the block in the chunk section
                    chunkSection.set(relativeX, relativeY, relativeZ, information.stateID());
                }
            }

            event.markForReEncode(true);
        } else if (event.getPacketType().equals(PacketType.Play.Server.BLOCK_CHANGE)) {

            WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(event);

            Player player = event.getPlayer();
            World world = player.getWorld();

            if (!plugin.blockService().existsAtWorld(world))
                return;

            FakeBlock fakeBlock = plugin.blockService().findByWorldNameAndPositon(
                    world.getName(),
                    packet.getBlockPosition().getX(),
                    packet.getBlockPosition().getY(),
                    packet.getBlockPosition().getZ()
            ).orElse(null);

            if (fakeBlock != null && fakeBlock.placed() && (fakeBlock.isViewer(player) || fakeBlock.autoViewable()) && (packet.getBlockId() != fakeBlock.stateID() && packet.getBlockId() != fakeBlock.oldStateID())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT)) {
            WrapperPlayClientPlayerBlockPlacement packet = new WrapperPlayClientPlayerBlockPlacement(event);
            Player player = event.getPlayer();
            World world = player.getWorld();

            if (plugin.blockService().existsAtPosition(packet.getBlockPosition(), world)) {
                event.setCancelled(true);
                Location location = new Location(
                        world,
                        packet.getBlockPosition().getX(),
                        packet.getBlockPosition().getY(),
                        packet.getBlockPosition().getZ()
                );

                MainHand hand = packet.getHand().equals(InteractionHand.MAIN_HAND) ? MainHand.RIGHT : MainHand.LEFT;
                FakeBlock fakeBlock = plugin.blockService().findByLocation(location).orElseThrow();

                new FakeBlockInteractEvent(fakeBlock, player, false, hand).callEvent();
            }
        } else if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_DIGGING)) {
            WrapperPlayClientPlayerDigging diggingPacket = new WrapperPlayClientPlayerDigging(event);

            Player player = event.getPlayer();
            World world = player.getWorld();

            if (plugin.blockService().existsAtPosition(diggingPacket.getBlockPosition(), world)) {

                Location location = new Location(
                        world,
                        diggingPacket.getBlockPosition().getX(),
                        diggingPacket.getBlockPosition().getY(),
                        diggingPacket.getBlockPosition().getZ()
                );

                FakeBlock fakeBlock = plugin.blockService().findByLocation(location).orElseThrow();
                if (diggingPacket.getAction() == DiggingAction.FINISHED_DIGGING) {
                    new FakeBlockBreakEvent(fakeBlock, player).callEvent();
                    event.setCancelled(true);
                    fakeBlock.silentPlace();
                    WrapperPlayServerAcknowledgeBlockChanges ack = new WrapperPlayServerAcknowledgeBlockChanges(diggingPacket.getSequence());
                    event.getUser().sendPacket(ack);
                } else {
                    WrapperPlayServerAcknowledgeBlockChanges ack = new WrapperPlayServerAcknowledgeBlockChanges(diggingPacket.getSequence());
                    event.getUser().sendPacket(ack);
                }


            }
        }
    }
}
