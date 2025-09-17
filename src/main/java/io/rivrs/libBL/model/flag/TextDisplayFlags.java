package io.rivrs.libBL.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TextDisplayFlags implements Flag {
    HAS_SHADOW(0x01),
    IS_SEE_THROUGH(0x02),
    USE_DEFAULT_BACKGROUND_COLOR(0x04),
    ;

    private final int value;
}
