package de.joshicodes.javashock.object;

import com.google.gson.JsonObject;
import de.joshicodes.javashock.JavaShock;
import de.joshicodes.javashock.action.RestAction;
import de.joshicodes.javashock.action.control.ControlData;
import de.joshicodes.javashock.action.control.ControlRequestAction;
import de.joshicodes.javashock.util.JsonUtil;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class Shocker {

    public static Shocker fromJson(final JavaShock instance, final JsonObject data) {
        final String id = JsonUtil.getString(data, "id");
        final String name = JsonUtil.getString(data, "name");
        final long rfId = JsonUtil.getLong(data, "rfId");
        final String model = JsonUtil.getString(data, "model");
        final boolean paused = JsonUtil.getBoolean(data, "isPaused");
        final String hubId = JsonUtil.getString(data, "device");
        final DeviceHub hub = instance.getHub(hubId);
        final Shocker shocker = new Shocker(instance, id, name, rfId, model, paused, hubId);
        instance.registerShocker(hub, shocker);
        return shocker;
    }

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

    public RestAction<Boolean> prepareControl(final ControlData data) {
        return new ControlRequestAction(instance).addShockControl(this, data);
    }

    public RestAction<Boolean> shock(final int intensity, final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.SHOCK, intensity, unit.toMillis(duration)));
    }

    public RestAction<Boolean> shock(final int intensity, final int duration) {
        return shock(intensity, duration, TimeUnit.MILLISECONDS);
    }

    public RestAction<Boolean> vibrate(final int intensity, final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.VIBRATE, intensity, unit.toMillis(duration)));
    }

    public RestAction<Boolean> vibrate(final int intensity, final int duration) {
        return vibrate(intensity, duration, TimeUnit.MILLISECONDS);
    }

    public RestAction<Boolean> sound(final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.SOUND, 0, unit.toMillis(duration)));
    }

    public RestAction<Boolean> sound(final int duration) {
        return sound(duration, TimeUnit.MILLISECONDS);
    }

    public RestAction<Boolean> stop() {
        return prepareControl(new ControlData(ControlData.ControlType.STOP, 0, 0));
    }

}
