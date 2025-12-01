package io.rivrs.libbl.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BeeFlags implements Flag {
    ANGRY(0x02),
    STUNG(0x04),
    NECTAR(0x08);

    private final int value;
}