package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;

public class FakeBlockPlaceEvent extends FakeBlockEvent implements Cancellable {

    private boolean cancelled;

    public FakeBlockPlaceEvent(FakeBlock fakeBlock) {
        super(!Bukkit.isPrimaryThread(), fakeBlock);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
