package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@Getter
public class FakeBlockInteractEvent extends FakeBlockEvent {

    private final Player player;
    private final boolean sneaking;
    private final MainHand hand;

    public FakeBlockInteractEvent(FakeBlock fakeBlock, Player player, boolean sneaking, MainHand hand) {
        super(true, fakeBlock);
        this.player = player;
        this.sneaking = sneaking;
        this.hand = hand;
    }

}
