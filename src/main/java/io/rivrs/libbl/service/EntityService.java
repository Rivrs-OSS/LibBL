package io.rivrs.libbl.service;

import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.task.EntityVisibilityTask;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class EntityService {

    private final LibBL plugin;
    private final List<PacketEntity> entities = new CopyOnWriteArrayList<>();
    private EntityVisibilityTask visibilityTask;

    public void init() {
        this.visibilityTask = new EntityVisibilityTask(this);
        this.visibilityTask.runTaskTimerAsynchronously(this.plugin, 0, 5);
    }

    public void shutdown() {
        this.entities.clear();
        this.visibilityTask.cancel();
    }

    public void register(PacketEntity entity) {
        this.entities.add(entity);
    }

    public void unregister(PacketEntity entity) {
        this.entities.remove(entity);
    }

    public void unregister(UUID uniqueId) {
        this.entities.removeIf(entity -> entity.uniqueId().equals(uniqueId));
    }

    public void unregister(int entityId) {
        this.entities.removeIf(entity -> entity.id() == entityId);
    }

    public Optional<PacketEntity> findByUniqueId(UUID uniqueId) {
        return this.entities.stream()
                .filter(entity -> entity.uniqueId().equals(uniqueId))
                .findFirst();
    }

    public <T extends PacketEntity> Optional<T> findByUniqueId(UUID uniqueId, Class<T> type) {
        return this.entities.stream()
                .filter(entity -> entity.uniqueId().equals(uniqueId))
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public Optional<PacketEntity> findById(int entityId) {
        return this.entities.stream()
                .filter(entity -> entity.id() == entityId)
                .findFirst();
    }

    public <T extends PacketEntity> Optional<T> findById(int entityId, Class<T> type) {
        return this.entities.stream()
                .filter(entity -> entity.id() == entityId)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public Optional<PacketEntity> findByLocation(Location location) {
        return this.entities.stream()
                .filter(entity -> entity.location().equals(location))
                .findFirst();
    }

    public <T extends PacketEntity> Optional<T> findByLocation(Location location, Class<T> type) {
        return this.entities.stream()
                .filter(entity -> entity.location().equals(location))
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public List<PacketEntity> findByWorld(World world) {
        return this.entities.stream()
                .filter(entity -> entity.location().getWorld().equals(world))
                .toList();
    }

    public <T extends PacketEntity> List<T> findByWorld(World world, Class<T> type) {
        return this.entities.stream()
                .filter(entity -> entity.location().getWorld().equals(world))
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    public <T extends PacketEntity> List<T> findByType(Class<T> type) {
        return this.entities.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    @Unmodifiable
    public List<PacketEntity> entities() {
        return Collections.unmodifiableList(this.entities);
    }
}
