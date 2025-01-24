package de.joshicodes.javashock;

import de.joshicodes.javashock.action.GetShockerAction;
import de.joshicodes.javashock.action.RestAction;
import de.joshicodes.javashock.object.DeviceHub;
import de.joshicodes.javashock.object.Shocker;
import de.joshicodes.javashock.request.RequestManager;
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
    private String applicationName = "JavaShock";

    private final RequestManager requestManager;

    private final List<DeviceHub> cachedHubs;

    public JavaShock(final String token) {
        this(token, DEFAULT_API_HOST);
    }

    public JavaShock(final String token, final String apiHost) {
        this.token = token;
        this.apiHost = apiHost;

        this.requestManager = new RequestManager(apiHost);

        this.cachedHubs = new ArrayList<>();

    }

    public RestAction<HashMap<DeviceHub, List<Shocker>>> retrieveAllShockers() {
        return new GetShockerAction(this);
    }

    public void registerHub(final DeviceHub hub, final List<Shocker> shockers) {
        hub.getShockers().addAll(shockers);
        cachedHubs.add(hub);
    }

    public Shocker getShocker(final String shockerId) {
        final Shocker cached = cachedHubs.stream().map(DeviceHub::getShockers).flatMap(List::stream).filter(shocker -> shocker.getId().equals(shockerId)).findFirst().orElse(null);
        if(cached != null) return cached;
        // TODO: Fetch shocker from API
        return null;
    }

    public DeviceHub getHub(String hubId) {
        final DeviceHub cached = cachedHubs.stream().filter(hub -> hub.getId().equals(hubId)).findFirst().orElse(null);
        if(cached != null) return cached;
        // TODO: Fetch hub from API
        return null;
    }

}
