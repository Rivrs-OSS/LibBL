package io.rivrs.libBL.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MobFlags implements Flag {
    NO_AI(0x01),
    LEFT_HANDED(0x02),
    AGGRESSIVE(0x04),
    ;

    private final int value;
}
