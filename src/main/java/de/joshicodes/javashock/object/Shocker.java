package de.joshicodes.javashock.object;

import de.joshicodes.javashock.JavaShock;
import de.joshicodes.javashock.action.RestAction;
import de.joshicodes.javashock.action.control.ControlData;
import de.joshicodes.javashock.action.control.ControlRequestAction;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class Shocker {

    private final JavaShock instance;

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final long rfId;

    @Getter
    private final String model;

    @Getter
    private final boolean paused;

    @Getter
    private final String hubId;

    /**
     * Creates a new Shocker Instance with the given id.
     * @param instance the JavaShock instance
     * @param id the id of the shocker
     */
    public Shocker(
            final JavaShock instance,
            final String id,
            final String name,
            final long rfId,
            final String model,
            final boolean paused,
            final String hubId
    ) {
        this.instance = instance;
        this.id = id;
        this.name = name;
        this.rfId = rfId;
        this.model = model;
        this.paused = paused;
        this.hubId = hubId;
    }

    public DeviceHub retrieveHub() {
        return instance.getHub(hubId);
    }

    public RestAction<Boolean> shock(final ControlData data) {
        return new ControlRequestAction(instance).addShockControl(this, data);
    }

    public RestAction<Boolean> shock(final int intensity, final int duration, final TimeUnit unit) {
        return shock(new ControlData(ControlData.ControlType.SHOCK, intensity, unit.toMillis(duration)));
    }

    public RestAction<Boolean> shock(final int intensity, final int duration) {
        return shock(intensity, duration, TimeUnit.MILLISECONDS);
    }

}
