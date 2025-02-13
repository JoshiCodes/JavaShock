package de.joshicodes.javashock.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.joshicodes.javashock.JavaShock;
import de.joshicodes.javashock.object.DeviceHub;
import de.joshicodes.javashock.object.Shocker;
import de.joshicodes.javashock.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetShockerAction extends RestAction<HashMap<DeviceHub, List<Shocker>>> {

    public GetShockerAction(final JavaShock instance) {
        super(
                instance,
                "/1/shockers/own",
                "GET",
                (resp) -> {
                    final JsonElement json = resp.getAsJsonElement();
                    final HashMap<DeviceHub, List<Shocker>> list = new HashMap<>();
                    if (json == null) return list;
                    if (!json.isJsonObject()) return list;
                    final JsonObject object = json.getAsJsonObject();
                    if (!object.has("data")) return list;
                    final JsonElement dataElement = object.get("data");
                    if (!dataElement.isJsonArray()) return list;
                    final JsonArray data = dataElement.getAsJsonArray();
                    for (final JsonElement hubElement : data) {
                        if (!hubElement.isJsonObject()) continue;
                        final JsonObject hubObject = hubElement.getAsJsonObject();
                        final String hubId = JsonUtil.getString(hubObject, "id");
                        final String hubName = JsonUtil.getString(hubObject, "name");
                        if (hubId == null || hubName == null) continue;
                        final DeviceHub hub = new DeviceHub(instance, hubId, hubName);
                        if (!hubObject.has("shockers")) continue;
                        final JsonElement shockersElement = hubObject.get("shockers");
                        if (!shockersElement.isJsonArray()) continue;
                        final JsonArray shockersArray = shockersElement.getAsJsonArray();
                        final List<Shocker> shockers = new ArrayList<>();
                        for (final JsonElement shockerElement : shockersArray) {
                            if (!shockerElement.isJsonObject()) continue;
                            final JsonObject shockerObject = shockerElement.getAsJsonObject();
                            final String name = JsonUtil.getString(shockerObject, "name");
                            final String id = JsonUtil.getString(shockerObject, "id");
                            final boolean isPaused = JsonUtil.getBoolean(shockerObject, "isPaused");
                            final long rfId = JsonUtil.getLong(shockerObject, "rfId");
                            final String model = JsonUtil.getString(shockerObject, "model");
                            if (name == null || id == null || model == null) continue;
                            final Shocker shocker = new Shocker(instance, id, name, rfId, model, isPaused, hub.getId());
                            shockers.add(shocker);
                        }
                        instance.registerHub(
                                hub,
                                shockers
                        );
                        list.put(hub, shockers);
                    }
                    return list;
                });
    }

}
