package io.rivrs.libbl.service;

import com.github.retrooper.packetevents.util.Vector3i;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.block.FakeBlock;
import io.rivrs.libbl.utils.ThreadSafeLong2ObjectMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class BlockService {

    private final LibBL plugin;

    private final Map<UUID, FakeBlock> fakeBlocks = new ConcurrentHashMap<>();
    private final Map<Location, UUID> positionUUIDMap = new ConcurrentHashMap<>();

    private final Map<String, ThreadSafeLong2ObjectMap<List<UUID>>> worldChunkUUIDMap = new ConcurrentHashMap<>();

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

        long chunkKey = getChunkKeyFromPosition(fakeBlock.position().x, fakeBlock.position().z);
        ThreadSafeLong2ObjectMap<List<UUID>> worldBlocks = this.worldChunkUUIDMap.computeIfAbsent(fakeBlock.worldName(), k -> new ThreadSafeLong2ObjectMap<>());
        List<UUID> chunkBlockList = worldBlocks.get(chunkKey);
        if (chunkBlockList == null) {
            chunkBlockList = new CopyOnWriteArrayList<>();
        }
        chunkBlockList.add(fakeBlock.uniqueID());
        worldBlocks.put(chunkKey, chunkBlockList);
        this.worldChunkUUIDMap.put(fakeBlock.worldName(), worldBlocks);

        this.positionUUIDMap.put(fakeBlock.location(), fakeBlock.uniqueID());
    }

    public void unregister(FakeBlock fakeBlock) {
        fakeBlock.remove();
        this.fakeBlocks.remove(fakeBlock.uniqueID());
        this.positionUUIDMap.remove(fakeBlock.location());
        ThreadSafeLong2ObjectMap<List<UUID>> worldBlocks = this.worldChunkUUIDMap.get(fakeBlock.worldName());
        if (worldBlocks != null) {
            long chunkKey = getChunkKeyFromPosition(fakeBlock.position().x, fakeBlock.position().z);
            List<UUID> chunkBlockList = worldBlocks.get(chunkKey);
            if (chunkBlockList != null) {
                chunkBlockList.remove(fakeBlock.uniqueID());
                worldBlocks.put(chunkKey, chunkBlockList);
                this.worldChunkUUIDMap.put(fakeBlock.worldName(), worldBlocks);
            }
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

    public Optional<FakeBlock> findByWorldNameAndPosition(String worldName, int x, int y, int z) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return Optional.empty();
        }
        Location location = new Location(world, x, y, z);
        return findByLocation(location);
    }

    public List<FakeBlock> findByChunk(int chunkX, int chunkZ, World world) {
        List<FakeBlock> blocksInChunk = new ArrayList<>();

        long chunkKey = Chunk.getChunkKey(chunkX, chunkZ);
        ThreadSafeLong2ObjectMap<List<UUID>> worldBlocks = this.worldChunkUUIDMap.get(world.getName());
        if (worldBlocks != null) {
            List<UUID> chunkBlockUUIDs = worldBlocks.get(chunkKey);
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
        ThreadSafeLong2ObjectMap<List<UUID>> worldBlockUUIDs = this.worldChunkUUIDMap.get(worldName);
        worldBlockUUIDs.forEach((key, value) -> {
            for (UUID uuid : value) {
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
        ThreadSafeLong2ObjectMap<List<UUID>> chunkMap = this.worldChunkUUIDMap.get(worldName);
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
    
    public void addBlockDataToCache(BlockData blockData) {
        if( this.dataStateCache.containsKey(blockData)) {
            return;
        }
        int stateID;
        try {
            stateID = SpigotConversionUtil.fromBukkitBlockData(blockData).getGlobalId();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to compute data state for block data: " + blockData + e.getMessage());
            return;
        }
        this.dataStateCache.put(blockData, stateID);
    }


    private long getChunkKeyFromPosition(int x, int z) {
        return Chunk.getChunkKey(x>>4, z>>4);
    }
}
