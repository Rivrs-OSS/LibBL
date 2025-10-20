package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@Getter
public class FakeBlockBreakEvent extends FakeBlockEvent {

    private final Player player;
    private final MainHand hand;

    public FakeBlockBreakEvent(FakeBlock fakeBlock, Player player, boolean sneaking, MainHand hand) {
        super(true, fakeBlock);
        this.player = player;
        this.hand = hand;
    }

}
