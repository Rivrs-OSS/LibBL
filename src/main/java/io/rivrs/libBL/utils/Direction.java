package io.rivrs.libBL.utils;

import lombok.Getter;

public enum Direction {
    // VarInt Enum Down = 0, Up = 1, North = 2, South = 3, West = 4, East = 5
    DOWN(0),
    UP(1),
    NORTH(2),
    SOUTH(3),
    WEST(4),
    EAST(5);
    @Getter
    private final int value;

    Direction(int value) {
        this.value = value;
    }
}
