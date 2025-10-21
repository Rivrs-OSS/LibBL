package io.rivrs.libbl.task;

import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.service.EntityService;
import io.rivrs.libbl.service.ViewerService;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@RequiredArgsConstructor
public class ViewerUpdateTask extends BukkitRunnable {

    private final ViewerService service;

    @Override
    public void run() {
        this.service.playerChannels().forEach((uuid, channel) -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()) {
                this.service.unregisterPlayerChannel(uuid);
            }
            if(channel == null){
                this.service.registerPlayer(uuid);
            }
        });
    }
}
