package io.rivrs.libbl.event;

import io.rivrs.libbl.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@Getter
public class PacketEntityAttackEvent extends PacketEntityEvent {

    private final Player player;
    private final boolean sneaking;
    private final MainHand hand;

    public PacketEntityAttackEvent(PacketEntity entity, Player player, boolean sneaking, MainHand hand) {
        super(true, entity);
        this.player = player;
        this.hand = hand;
        this.sneaking = sneaking;
    }

}
