package io.rivrs.libBL.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityFlags implements Flag {
    FIRE(0x01),
    SNEAKING(0x02),
    @Deprecated
    RIDING(0x04),
    SPRINTING(0x08),
    SWIMMING(0x10),
    INVISIBLE(0x20),
    GLOWING(0x40),
    ELYTRA_FLYING(0x80);

    private final int value;
}
