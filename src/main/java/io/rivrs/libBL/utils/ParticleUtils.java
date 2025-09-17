package io.rivrs.libBL.utils;

import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class ParticleUtils {

    public static WrapperPlayServerParticle create(Location location, Particle<?> particle, Vector3f offset, float maxSpeed, int count) {
        return new WrapperPlayServerParticle(
                particle,
                false,
                new Vector3d(location.getX(), location.getY(), location.getZ()),
                new Vector3f(offset.getX(), offset.getY(), offset.getZ()),
                maxSpeed,
                count
        );
    }

    public static WrapperPlayServerParticle create(Location location, Particle<?> particle) {
        return create(location, particle, Vector3f.zero(), 0, 1);
    }

    public static WrapperPlayServerParticle create(Location location, org.bukkit.Particle particle) {
        return create(location, new Particle<>(SpigotConversionUtil.fromBukkitParticle(particle)));
    }
}