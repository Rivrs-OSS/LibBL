package io.rivrs.libBL.model.flag;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BatFlags implements Flag {
    HANGING(0x01)
    ;

    private final int value;
}
