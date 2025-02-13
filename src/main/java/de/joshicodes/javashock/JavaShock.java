package de.joshicodes.javashock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.javashock.action.GetShockerAction;
import de.joshicodes.javashock.action.RestAction;
import de.joshicodes.javashock.action.SimpleAction;
import de.joshicodes.javashock.object.DeviceHub;
import de.joshicodes.javashock.object.Shocker;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JavaShock {

    public static final String DEFAULT_API_HOST = "https://api.openshock.app";
    public static final int MAX_SHOCK_DURATION = 30000;

    @Getter
    private final String token;
    @Getter
    private final String apiHost;

    @Setter
    @Getter
    private String customName = "JavaShock";

    private final List<DeviceHub> cachedHubs;

    public JavaShock(final String token) {
        this(token, DEFAULT_API_HOST);
    }

    public JavaShock(final String token, final String apiHost) {
        this.token = token;
        this.apiHost = apiHost;

        this.cachedHubs = new ArrayList<>();

        // Retrieve all shockers and cache them
        retrieveAllShockers().queue();

    }

    /**
     * Retrieves all shockers and their hubs from the API.
     * They will automatically be cached.
     *
     * @return The RestAction to queue or execute
     */
    public RestAction<HashMap<DeviceHub, List<Shocker>>> retrieveAllShockers() {
        return new GetShockerAction(this);
    }

    /**
     * Caches a hub with its shockers.
     * <b>Does NOT create a new hub or shocker for the user.</b>
     * This Method is designed to be used internally, use with caution.
     *
     * @param hub      The hub to cache
     * @param shockers The shockers to cache
     */
    public void registerHub(final DeviceHub hub, final List<Shocker> shockers) {
        hub.getShockers().addAll(shockers);
        cachedHubs.add(hub);
    }

    /**
     * Caches a shocker in a hub.
     * <b>Does NOT create a new hub or shocker for the user.</b>
     * This Method is designed to be used internally, use with caution.
     *
     * @param hub     The hub to cache the shocker in
     * @param shocker The shocker to cache
     */
    public void registerShocker(final DeviceHub hub, final Shocker shocker) {
        hub.getShockers().removeIf(s -> s.getId().equals(shocker.getId()));
        hub.getShockers().add(shocker);
        // replace hub in cachedHubs
        cachedHubs.removeIf(h -> h.getId().equals(hub.getId()));
        cachedHubs.add(hub);
    }

    /**
     * Retrieves a shocker by its ID.
     * If the shocker is not cached, it will be fetched from the API.
     *
     * @param shockerId The ID of the shocker
     * @return The shocker or null if it does not exist
     * @see #getCachedShocker(String) #getCachedShocker(String) - to fetch the shocker from the cache
     * @see #retrieveShocker(String) #retrieveShocker(String) - to always fetch the shocker from the API
     */
    public RestAction<Shocker> getShocker(final String shockerId) {
        if (cachedHubs
                .stream()
                .anyMatch(hub -> hub.getShockers().stream().anyMatch(shock -> shock.getId().equals(shockerId)))
        ) {
            return new SimpleAction<>(
                    this,
                    () -> cachedHubs
                            .stream()
                            .map(DeviceHub::getShockers)
                            .flatMap(List::stream)
                            .filter(shocker -> shocker.getId().equals(shockerId))
                            .findFirst()
                            .orElse(null)
            );
        }
        return retrieveShocker(shockerId);
    }

    /**
     * Retrieves a shocker by its ID.
     * If the shocker is not cached, null will be returned.
     *
     * @param shockerId The ID of the shocker
     * @return The shocker or null if it does not exist
     * @see #getShocker(String) - to fetch the shocker from cache or alternatively from the API
     * @see #retrieveShocker(String)  - to always fetch the shocker from the API
     */
    public Shocker getCachedShocker(final String shockerId) {
        return cachedHubs.stream().map(DeviceHub::getShockers).flatMap(List::stream).filter(shocker -> shocker.getId().equals(shockerId)).findFirst().orElse(null);
    }

    /**
     * Retrieves a shocker by its ID.
     * This method will always fetch the shocker from the API.
     * Try to use {@link #getShocker(String)} instead.
     *
     * @param shockerId The ID of the shocker
     * @return The RestAction to queue or execute
     * @see #getShocker(String) - to fetch the shocker from cache or alternatively from the API
     * @see #getCachedShocker(String) - to fetch the shocker from the cache
     */
    public RestAction<Shocker> retrieveShocker(final String shockerId) {
        return new RestAction<>(
                this,
                "/1/shockers/" + shockerId,
                "GET",
                resp -> {
                    final JsonElement element = resp.getAsJsonElement();
                    if (element == null) return null;
                    if (!element.isJsonObject()) return null;
                    final JsonObject object = element.getAsJsonObject();
                    if (!object.has("data")) return null;
                    final JsonObject data = object.getAsJsonObject("data");
                    return Shocker.fromJson(this, data);
                }
        );
    }

    /**
     * Retrieves a hub by its ID.
     * If hubs are fetched from the API, they do not contain their shockers.
     * Use {@link #retrieveAllShockers()} to retrieve all shockers and hubs instead.
     * <b>This method does not fetch the hub from the api.</b>
     *
     * @param hubId The ID of the hub
     * @return The hub or null if it does not exist
     */
    public DeviceHub getHub(String hubId) {
        if (
                cachedHubs
                        .stream()
                        .anyMatch(hub -> hub.getId().equals(hubId))
        ) {
            return cachedHubs
                    .stream()
                    .filter(hub -> hub.getId().equals(hubId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

}
