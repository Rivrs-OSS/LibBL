package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import org.bukkit.Bukkit;

public class FakeBlockRemoveEvent extends FakeBlockEvent {

    public FakeBlockRemoveEvent(FakeBlock fakeBlock) {
        super(!Bukkit.isPrimaryThread(), fakeBlock);
    }

}
