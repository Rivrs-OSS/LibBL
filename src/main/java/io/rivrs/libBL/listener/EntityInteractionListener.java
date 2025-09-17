package io.rivrs.libBL.listener;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import io.rivrs.libBL.LibBL;
import io.rivrs.libBL.event.PacketEntityAttackEvent;
import io.rivrs.libBL.event.PacketEntityInteractEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@RequiredArgsConstructor
public class EntityInteractionListener extends SimplePacketListenerAbstract {

    private final LibBL plugin;

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (!event.getPacketType().equals(PacketType.Play.Client.INTERACT_ENTITY))
            return;

        WrapperPlayClientInteractEntity packet = new WrapperPlayClientInteractEntity(event);
        this.plugin.entities()
                .findById(packet.getEntityId())
                .ifPresent(packetEntity -> {
                    event.setCancelled(true);

                    Player player = event.getPlayer();
                    MainHand hand = packet.getHand().equals(InteractionHand.MAIN_HAND) ? MainHand.RIGHT : MainHand.LEFT;

                    if (packet.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) {
                        new PacketEntityAttackEvent(packetEntity, player, packet.isSneaking().orElse(false), hand).callEvent();
                        return;
                    }

                    new PacketEntityInteractEvent(packetEntity, player, packet.isSneaking().orElse(false), hand).callEvent();
                });
    }

    @Override
    public PacketListenerPriority getPriority() {
        return PacketListenerPriority.MONITOR;
    }
}
