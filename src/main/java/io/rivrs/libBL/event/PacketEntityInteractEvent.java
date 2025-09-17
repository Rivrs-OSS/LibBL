package io.rivrs.libBL.event;

import io.rivrs.libBL.model.entities.PacketEntity;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@Getter
public class PacketEntityInteractEvent extends PacketEntityEvent {

    private final Player player;
    private final boolean sneaking;
    private final MainHand hand;

    public PacketEntityInteractEvent(PacketEntity entity, Player player, boolean sneaking, MainHand hand) {
        super(true, entity);
        this.player = player;
        this.sneaking = sneaking;
        this.hand = hand;
    }

}
