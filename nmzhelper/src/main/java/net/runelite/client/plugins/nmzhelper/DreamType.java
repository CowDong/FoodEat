package net.runelite.client.plugins.nmzhelper;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DreamType {
    NOT_SUPPORTED(-1),
    NOT_DREAMING(0),
    CUSTOMISABLE_RUMBLE_HARD(123);

    private static final Map<Integer, DreamType> lookup = Stream.of(DreamType.values()).collect(Collectors.toMap(DreamType::getId, Function.identity()));
    private final int id;

    DreamType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static DreamType fromId(int id) {
        DreamType dreamType = lookup.get(id);
        return dreamType == null ? NOT_SUPPORTED : dreamType;
    }
}
