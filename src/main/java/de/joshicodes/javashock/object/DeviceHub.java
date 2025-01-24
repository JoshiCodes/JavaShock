package de.joshicodes.javashock.object;

import de.joshicodes.javashock.JavaShock;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DeviceHub {

    private final JavaShock instance;

    @Getter
    private final String id;
    @Getter
    private final String name;

    @Getter
    private final List<Shocker> shockers;

    public DeviceHub(final JavaShock instance, final String id, final String name) {
        this.instance = instance;
        this.id = id;
        this.name = name;

        this.shockers = new ArrayList<>();
    }

}
