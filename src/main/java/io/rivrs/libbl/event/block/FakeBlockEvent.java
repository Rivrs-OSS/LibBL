package io.rivrs.libbl.event.block;

import io.rivrs.libbl.model.block.FakeBlock;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class FakeBlockEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final FakeBlock fakeBlock;

    public FakeBlockEvent(FakeBlock fakeBlock) {
        this.fakeBlock = fakeBlock;
    }

    public FakeBlockEvent(boolean isAsync, FakeBlock fakeBlock) {
        super(isAsync);
        this.fakeBlock = fakeBlock;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
