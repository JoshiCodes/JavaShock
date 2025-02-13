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
     *
     * @param instance the JavaShock instance
     * @param id       the id of the shocker
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

    /**
     * Gets the corresponding DeviceHub of this Shocker.
     *
     * @return the DeviceHub
     */
    public DeviceHub getHub() {
        return instance.getHub(hubId);
    }

    /**
     * Prepares a control action for this Shocker.
     * If you want to execute a specific action, you should use one of the following methods:
     * <ul>
     *     <li>{@link #shock(int, int, TimeUnit)}</li>
     *     <li>{@link #vibrate(int, int, TimeUnit)}</li>
     *     <li>{@link #sound(int, TimeUnit)}</li>
     *     <li>{@link #stop()}</li>
     * </ul>
     *
     * @param data the control data
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #shock(int, int, TimeUnit)
     * @see #vibrate(int, int, TimeUnit)
     * @see #sound(int, TimeUnit)
     * @see #stop()
     */
    public RestAction<Boolean> prepareControl(final ControlData data) {
        return new ControlRequestAction(instance).addShockControl(this, data);
    }

    /**
     * Sends a shock with the given intensity and duration.
     *
     * @param intensity the intensity of the shock
     * @param duration  the duration of the shock
     * @param unit      the time unit of the duration
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #shock(int, int)
     */
    public RestAction<Boolean> shock(final int intensity, final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.SHOCK, intensity, unit.toMillis(duration)));
    }

    /**
     * Sends a shock with the given intensity and duration.
     *
     * @param intensity the intensity
     * @param duration  the duration in seconds
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #shock(int, int, TimeUnit)
     */
    public RestAction<Boolean> shock(final int intensity, final int duration) {
        return shock(intensity, duration, TimeUnit.SECONDS);
    }

    /**
     * Sends a vibration with the given intensity and duration.
     *
     * @param intensity the intensity
     * @param duration  the duration
     * @param unit      the time unit of the duration
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #vibrate(int, int)
     */
    public RestAction<Boolean> vibrate(final int intensity, final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.VIBRATE, intensity, unit.toMillis(duration)));
    }

    /**
     * Sends a vibration with the given intensity and duration.
     *
     * @param intensity the intensity
     * @param duration  the duration in seconds
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #vibrate(int, int, TimeUnit)
     */
    public RestAction<Boolean> vibrate(final int intensity, final int duration) {
        return vibrate(intensity, duration, TimeUnit.SECONDS);
    }

    /**
     * Sends a sound with the given duration.
     *
     * @param duration the duration
     * @param unit     the time unit of the duration
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #sound(int)
     */
    public RestAction<Boolean> sound(final int duration, final TimeUnit unit) {
        return prepareControl(new ControlData(ControlData.ControlType.SOUND, 0, unit.toMillis(duration)));
    }

    /**
     * Sends a sound with the given duration.
     *
     * @param duration the duration in seconds
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     * @see #sound(int, TimeUnit)
     */
    public RestAction<Boolean> sound(final int duration) {
        return sound(duration, TimeUnit.SECONDS);
    }

    /**
     * Stops the current action of the Shocker.
     *
     * @return the RestAction, use {@link RestAction#queue()} or {@link RestAction#execute()} to execute the action
     */
    public RestAction<Boolean> stop() {
        return prepareControl(new ControlData(ControlData.ControlType.STOP, 0, 0));
    }

}
