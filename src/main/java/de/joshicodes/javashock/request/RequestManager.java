package de.joshicodes.javashock.request;

import com.google.gson.JsonElement;
import de.joshicodes.javashock.action.RestAction;

public class RequestManager {

    private final String API_URL;

    public RequestManager(final String API_URL) {
        this.API_URL = API_URL;
    }

}
