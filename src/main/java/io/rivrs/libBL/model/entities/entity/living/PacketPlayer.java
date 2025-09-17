package io.rivrs.libBL.model.entities.entity.living;

import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.rivrs.libBL.model.entities.entity.PacketLivingEntity;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.UUID;

@Getter
public class PacketPlayer extends PacketLivingEntity {

    private final String name;
    private final @Nullable Skin skin;
    private @Nullable Component displayName;

    public PacketPlayer(UUID uniqueId, String name, @Nullable Component displayName, @Nullable Skin skin, Location location) {
        super(uniqueId, EntityType.PLAYER, location);
        this.name = name;
        this.displayName = displayName;
        this.skin = skin;
    }

    public PacketPlayer displayName(Component displayName) {
        this.displayName = displayName;
        this.sendPacket(this.builtPlayerInfoUpdatePacket(EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME)));
        return this;
    }


    @Override
    protected LinkedList<PacketWrapper<?>> buildSpawnPackets(Player player) {
        LinkedList<PacketWrapper<?>> packets = super.buildSpawnPackets(player);
        packets.addFirst(this.builtPlayerInfoUpdatePacket(EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER)));
        packets.addFirst(this.builtPlayerInfoUpdatePacket(EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME)));
        return packets;
    }

    @Override
    protected LinkedList<PacketWrapper<?>> buildDestroyPackets() {
        LinkedList<PacketWrapper<?>> packets = super.buildDestroyPackets();
        packets.addFirst(new WrapperPlayServerPlayerInfoRemove(this.uniqueId));
        return packets;
    }

    protected WrapperPlayServerPlayerInfoUpdate builtPlayerInfoUpdatePacket(EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions) {
        return new WrapperPlayServerPlayerInfoUpdate(
                actions,
                this.builtPlayerDataPacket()
        );
    }

    protected WrapperPlayServerPlayerInfoUpdate.PlayerInfo builtPlayerDataPacket() {
        return new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                this.buildUserProfile(),
                false,
                0,
                GameMode.SURVIVAL,
                this.displayName,
                null
        );
    }

    protected UserProfile buildUserProfile() {
        UserProfile profile = new UserProfile(
                this.uniqueId,
                this.name
        );

        if (this.skin != null)
            profile.getTextureProperties().add(new TextureProperty(
                    "textures",
                    this.skin.value,
                    this.skin.signature
            ));

        return profile;
    }

    public record Skin(String value, String signature) {
    }

}
