package io.rivrs.libbl.task;

import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
            if(location == null)
                continue;
            if(!location.isWorldLoaded())
                continue;
            if(location.getWorld() == null)
                continue;
            if (!location.isChunkLoaded())
                continue;

            // Remove viewers that are too far away
            for (UUID uuid : entity.viewers()) {
                Player viewer = Bukkit.getPlayer(uuid);
                if (viewer == null || !viewer.isOnline()){
                    entity.removeViewer(viewer);
                    continue;
                }
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
