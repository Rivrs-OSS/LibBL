package io.rivrs.libbl.listener;

import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.service.EntityService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final EntityService service;

    public PlayerListener(LibBL plugin, EntityService service) {
        this.service = service;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (PacketEntity entity : this.service.entities()) {
            if (!entity.alive()
                    || !entity.autoViewable())
                continue;
            entity.removeViewer(event.getPlayer());
        }
    }

}
