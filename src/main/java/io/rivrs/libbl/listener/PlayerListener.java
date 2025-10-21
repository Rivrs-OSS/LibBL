package io.rivrs.libbl.listener;

import io.rivrs.libbl.LibBL;
import io.rivrs.libbl.model.entities.PacketEntity;
import io.rivrs.libbl.service.EntityService;
import io.rivrs.libbl.service.ViewerService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final EntityService service;
    private final ViewerService viewerService;

    public PlayerListener(LibBL plugin, EntityService service, ViewerService viewerService) {
        this.viewerService = viewerService;
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
        viewerService.unregisterPlayerChannel(event.getPlayer().getUniqueId());
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        viewerService.registerPlayer(event.getPlayer());
    }

}
