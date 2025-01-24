package de.joshicodes.javashock.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.joshicodes.javashock.action.RestAction;

public class JsonUtil {

    public static String getString(final JsonObject object, final String key, final String def) {
        return object.has(key) ? object.get(key).getAsString() : def;
    }

    public static String getString(final JsonObject object, final String key) {
        return getString(object, key, null);
    }

    public static long getLong(final JsonObject object, final String key, final long def) {
        return object.has(key) ? object.get(key).getAsLong() : def;
    }

    public static long getLong(final JsonObject object, final String key) {
        return getLong(object, key, 0);
    }

    public static boolean getBoolean(final JsonObject shockerObject, final String key, final boolean def) {
        return shockerObject.has(key) ? shockerObject.get(key).getAsBoolean() : def;
    }

    public static boolean getBoolean(final JsonObject shockerObject, final String key) {
        return getBoolean(shockerObject, key, false);
    }

    public static JsonElement parse(RestAction.RestResponse resp) {
        final String body = resp.rawBody();
        if(body != null)
            return JsonParser.parseString(body);
        return null;
    }

}
