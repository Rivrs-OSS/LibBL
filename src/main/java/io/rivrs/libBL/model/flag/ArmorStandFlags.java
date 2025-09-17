package io.rivrs.libBL.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ArmorStandFlags implements Flag {
    IS_SMALL(0x01),
    UNUSED(0x02),
    HAS_ARMS(0x04),
    HAS_NO_BASE_PLATE(0x08),
    IS_MARKER(0x10);

    private final int value;
}
