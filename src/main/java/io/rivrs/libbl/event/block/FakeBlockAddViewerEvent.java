package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter
public class FakeBlockAddViewerEvent extends FakeBlockEvent implements Cancellable {

    private final Player player;
    private final Reason reason;
    private boolean cancelled;

    public FakeBlockAddViewerEvent(FakeBlock fakeBlock, Player player, Reason reason) {
        super(!Bukkit.isPrimaryThread(), fakeBlock);
        this.player = player;
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public enum Reason {
        BLOCK_PLACE,
        PLUGIN
    }
}
