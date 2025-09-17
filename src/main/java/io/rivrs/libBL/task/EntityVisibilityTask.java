package io.rivrs.libBL.task;

import io.rivrs.libBL.LibBL;
import io.rivrs.libBL.model.entities.PacketEntity;
import io.rivrs.libBL.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class EntityVisibilityTask extends BukkitRunnable {

    private final EntityService service;

    @Override
    public void run() {
        for (PacketEntity entity : this.service.entities()) {
            if (!entity.alive()
                || !entity.autoViewable())
                continue;

            Location location = entity.location();
            if (!location.isChunkLoaded())
                return;

            // Remove viewers that are too far away
            for (Player viewer : entity.viewersAsPlayers()) {
                if (!viewer.getWorld().equals(location.getWorld())
                    || viewer.getLocation().distanceSquared(location) >= LibBL.ENTITY_SIMULATION_DISTANCE_SQR())
                    entity.removeViewer(viewer);
            }

            // Add nearby players as viewers
            for (Player onlinePlayer : location.getWorld().getPlayers()) {
                if (!entity.isViewer(onlinePlayer)
                    && onlinePlayer.getLocation().distanceSquared(location) < LibBL.ENTITY_SIMULATION_DISTANCE_SQR())
                    entity.addViewer(onlinePlayer);
            }
        }

    }
}
