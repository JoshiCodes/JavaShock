package de.joshicodes.javashock.action.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.joshicodes.javashock.JavaShock;
import de.joshicodes.javashock.action.RestAction;
import de.joshicodes.javashock.object.Shocker;

import java.util.HashMap;

public class ControlRequestAction extends RestAction<Boolean> {

    private final HashMap<Shocker, ControlData> data;

    public ControlRequestAction(JavaShock instance) {
        super(instance, "/2/shockers/control", "POST", resp -> {
            if(resp.httpResponse().statusCode() != 200)
                throw new RuntimeException("Request failed with status code " + resp.httpResponse().statusCode());
            return true;
        });
        data = new HashMap<>();
        contentType = "application/json";
    }

    public RestAction<Boolean> addShockControl(Shocker shocker, ControlData data) {
        final int intensity = data.intensity();
        if(intensity < 0 || intensity > 100)
            throw new IllegalArgumentException("Intensity must be between 0 and 100");
        final long duration = data.duration();
        if(duration < 0 || duration > JavaShock.MAX_SHOCK_DURATION)
            throw new IllegalArgumentException("Duration must be between 0 and " + JavaShock.MAX_SHOCK_DURATION);
        this.data.put(shocker, data);
        return this;
    }

    @Override
    protected String prepareBody() {
        final JsonObject object = new JsonObject();
        object.addProperty("customName", instance.getApplicationName());
        final JsonArray shocks = new JsonArray();
        data.forEach((shocker, controlData) -> {
            final JsonObject shock = new JsonObject();
            shock.addProperty("id", shocker.getId());
            shock.addProperty("type", controlData.type().getName());
            shock.addProperty("intensity", controlData.intensity());
            shock.addProperty("duration", controlData.duration());
            shocks.add(shock);
        });
        object.add("shocks", shocks);
        return object.toString();
    }

}
