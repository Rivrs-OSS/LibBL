package io.rivrs.libBL.model.flag;

public interface Flag {

    int value();

    static byte toBitMask(Flag... flags) {
        int mask = 0;
        for (Flag flag : flags)
            mask |= flag.value();
        return (byte) mask;
    }
}
