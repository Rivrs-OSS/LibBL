package io.rivrs.libbl.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ArrowFlags implements Flag {
    IS_CRITICAL(0x01),
    IS_NO_CLIP(0x02),
    ;

    private final int value;
}
