package io.rivrs.libbl;

import com.github.retrooper.packetevents.PacketEvents;
import io.rivrs.libbl.listener.EntityInteractionListener;
import io.rivrs.libbl.listener.PlayerListener;
import io.rivrs.libbl.service.EntityService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LibBL extends JavaPlugin {

    private static LibBL instance;

    @Getter
    private static int ENTITY_SIMULATION_DISTANCE_SQR;
    @Getter
    private static int RENDER_DISTANCE_SQR;

    private EntityService entities;

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

        this.entities = new EntityService(this);
        this.entities.init();

        new PlayerListener(this, this.entities);

        // Listeners
        PacketEvents.getAPI().getEventManager().registerListener(new EntityInteractionListener(this));

        instance = this;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Service
        this.entities.shutdown();

        instance = null;
    }
}
