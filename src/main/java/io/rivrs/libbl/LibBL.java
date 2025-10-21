package io.rivrs.libbl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.rivrs.libbl.listener.EntityInteractionListener;
import io.rivrs.libbl.listener.MapListener;
import io.rivrs.libbl.listener.PlayerListener;
import io.rivrs.libbl.service.BlockService;
import io.rivrs.libbl.service.EntityService;
import io.rivrs.libbl.service.ViewerService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LibBL extends JavaPlugin {

    private static LibBL instance;

    @Getter
    private static int ENTITY_SIMULATION_DISTANCE_SQR;
    @Getter
    private static int RENDER_DISTANCE_SQR;

    private EntityService entityService;
    private BlockService blockService;
    private ViewerService viewerService;

    public static LibBL get() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        int simulationDistance = this.getServer().getSimulationDistance() * 16;
        ENTITY_SIMULATION_DISTANCE_SQR = simulationDistance * simulationDistance;
        int renderDistance = this.getServer().getViewDistance() * 16;
        RENDER_DISTANCE_SQR = renderDistance * renderDistance;

        this.entityService = new EntityService(this);
        this.entityService.init();

        this.blockService = new BlockService(this);
        this.blockService.init();

        this.viewerService = new ViewerService(this);
        this.viewerService.init();

        new PlayerListener(this, this.entityService, this.viewerService);

        // Listeners
        PacketEvents.getAPI().getEventManager().registerListener(new EntityInteractionListener(this));
        PacketEvents.getAPI().getEventManager().registerListener(new MapListener(this), PacketListenerPriority.MONITOR);

        instance = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Service
        this.entityService.shutdown();
        this.blockService.shutdown();
        this.viewerService.shutdown();

        instance = null;
    }
}
