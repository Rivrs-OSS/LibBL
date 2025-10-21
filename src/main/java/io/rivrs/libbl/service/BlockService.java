package io.rivrs.libbl.service;

import com.github.retrooper.packetevents.util.Vector3i;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.block.FakeBlock;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector2i;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class BlockService {

    private final LibBL plugin;

    private final Map<UUID, FakeBlock> fakeBlocks = new ConcurrentHashMap<>();
    private final Map<Location, UUID> positionUUIDMap = new ConcurrentHashMap<>();

    private final Map<String, Map<Vector2i, List<UUID>>> worldChunkUUIDMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<BlockData, Integer> dataStateCache = new ConcurrentHashMap<>();

    BukkitRunnable cleanupTask = new BukkitRunnable() {
        @Override
        public void run() {
            cleanUp();
        }
    };

    public void init() {
        this.cleanupTask.runTaskTimerAsynchronously(this.plugin, 0, 200L);
    }

    public void shutdown() {
        this.fakeBlocks.clear();
    }

    public void register(FakeBlock fakeBlock) {
        if (this.positionUUIDMap.containsKey(fakeBlock.location())) {
            UUID existingUUID = this.positionUUIDMap.get(fakeBlock.location());
            FakeBlock existingBlock = this.fakeBlocks.get(existingUUID);
            if (existingBlock != null) {
                unregister(existingBlock);
            }
        }
        this.fakeBlocks.put(fakeBlock.uniqueID(), fakeBlock);
        Map<Vector2i, List<UUID>> worldBlocks = this.worldChunkUUIDMap.computeIfAbsent(fakeBlock.worldName(), k -> new ConcurrentHashMap<>());
        Vector2i chunkPos = new Vector2i(fakeBlock.position().x >> 4, fakeBlock.position().z >> 4);
        List<UUID> worldBlockList = worldBlocks.computeIfAbsent(chunkPos, k -> new CopyOnWriteArrayList<>());
        worldBlockList.add(fakeBlock.uniqueID());
        worldBlocks.put(chunkPos, worldBlockList);
        this.worldChunkUUIDMap.put(fakeBlock.worldName(), worldBlocks);

        this.positionUUIDMap.put(fakeBlock.location(), fakeBlock.uniqueID());
    }

    public void unregister(FakeBlock fakeBlock) {
        fakeBlock.remove();
        this.fakeBlocks.remove(fakeBlock.uniqueID());
        this.positionUUIDMap.remove(fakeBlock.location());
        Map<Vector2i, List<UUID>> worldBlocksMap = this.worldChunkUUIDMap.get(fakeBlock.worldName());
        if (worldBlocksMap != null) {
            Vector2i chunkPos = new Vector2i(fakeBlock.position().x >> 4, fakeBlock.position().z >> 4);
            List<UUID> chunkBlockList = worldBlocksMap.get(chunkPos);
            if (chunkBlockList != null) {
                chunkBlockList.remove(fakeBlock.uniqueID());
                worldBlocksMap.put(chunkPos, chunkBlockList);
            }


            this.worldChunkUUIDMap.put(fakeBlock.worldName(), worldBlocksMap);
        }
    }

    public Optional<FakeBlock> findByUUID(UUID uuid) {
        FakeBlock fakeBlock = this.fakeBlocks.get(uuid);
        return Optional.ofNullable(fakeBlock);
    }

    public Optional<FakeBlock> findByLocation(Location location) {
        UUID fakeBlockUUID = this.positionUUIDMap.get(location);
        if (fakeBlockUUID == null) {
            return Optional.empty();
        }
        FakeBlock fakeBlock = this.fakeBlocks.get(fakeBlockUUID);
        return Optional.ofNullable(fakeBlock);
    }

    public Optional<FakeBlock> findByWorldNameAndPositon(String worldName, int x, int y, int z) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Optional.empty();
        }
        Location location = new Location(world, x, y, z);
        return findByLocation(location);
    }

    public List<FakeBlock> findByChunk(int chunkX, int chunkZ, World world) {
        List<FakeBlock> blocksInChunk = new ArrayList<>();

        Map<Vector2i, List<UUID>> worldBlocks = this.worldChunkUUIDMap.get(world.getName());
        if (worldBlocks != null) {
            List<UUID> chunkBlockUUIDs = worldBlocks.get(new Vector2i(chunkX, chunkZ));
            if (chunkBlockUUIDs != null) {
                for (UUID uuid : chunkBlockUUIDs) {
                    FakeBlock block = this.fakeBlocks.get(uuid);
                    if (block != null && block.placed()) {
                        blocksInChunk.add(block);
                    }
                }
            }
        }

        return blocksInChunk;
    }

    public List<FakeBlock> findByWorld(World world) {
        return findByWorldName(world.getName());
    }

    public List<FakeBlock> findByWorldName(String worldName) {
        List<FakeBlock> blocksInWorld = new ArrayList<>();
        Collection<List<UUID>> worldBlockUUIDs = this.worldChunkUUIDMap.get(worldName).values();
        worldBlockUUIDs.forEach(worldBlockList -> {
            for (UUID uuid : worldBlockList) {
                FakeBlock block = this.fakeBlocks.get(uuid);
                if (block != null) {
                    blocksInWorld.add(block);
                }
            }
        });
        return blocksInWorld;
    }

    public boolean existsAtPosition(Location location) {
        return this.positionUUIDMap.containsKey(location);
    }

    public boolean existsAtPosition(Vector3i location, World world) {
        Location bukkitLocation = new Location(world, location.x, location.y, location.z);
        return this.positionUUIDMap.containsKey(bukkitLocation);
    }

    public boolean existsAtWorld(World world) {
        return this.worldChunkUUIDMap.containsKey(world.getName());
    }

    @Unmodifiable
    public Map<UUID, FakeBlock> fakeBlocks() {
        return Map.copyOf(this.fakeBlocks);
    }

    private void cleanUp() {
        worldChunkUUIDMap.keySet().forEach(this::cleanUpWorld);
    }

    public void cleanUpWorld(String worldName) {
        if (!this.worldChunkUUIDMap.containsKey(worldName))
            return;
        Map<Vector2i, List<UUID>> chunkMap = this.worldChunkUUIDMap.get(worldName);
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            chunkMap.forEach((chunkPos, uuidList) -> {
                for (UUID uuid : uuidList) {
                    FakeBlock fakeBlock = this.fakeBlocks.get(uuid);
                    if (fakeBlock != null) {
                        unregister(fakeBlock);
                    }
                }
            });
            worldChunkUUIDMap.remove(worldName);
        }
    }

    @Nullable
    public Integer getDataState(BlockData blockData) {
        return this.dataStateCache.computeIfAbsent(blockData, bd -> {
            try {
                return SpigotConversionUtil.fromBukkitBlockData(bd).getGlobalId();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to compute data state for block data: " + bd + e.getMessage());
                return null;
            }
        });
    }
}
