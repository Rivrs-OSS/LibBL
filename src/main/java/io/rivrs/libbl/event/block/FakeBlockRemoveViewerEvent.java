package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class FakeBlockRemoveViewerEvent extends FakeBlockEvent {

    private final Player player;
    private final Reason reason;

    public FakeBlockRemoveViewerEvent(FakeBlock fakeBlock, Player player, Reason reason) {
        super(!Bukkit.isPrimaryThread(), fakeBlock);
        this.player = player;
        this.reason = reason;
    }

    public enum Reason {
        BLOCK_REMOVED,
        PLUGIN
    }
}
