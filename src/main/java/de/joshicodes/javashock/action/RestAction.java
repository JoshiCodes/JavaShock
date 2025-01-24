package de.joshicodes.javashock.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.joshicodes.javashock.JavaShock;
import lombok.Getter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RestAction<T> {

    protected final JavaShock instance;

    @Getter
    private final String endpoint;
    @Getter
    private final String method;

    protected Function<HttpRequest.Builder, HttpRequest.Builder> clientModifier;
    protected final Function<RestResponse, T> responseHandler;

    protected String contentType;

    public RestAction(final JavaShock instance, final String endpoint, final String method, final Function<HttpRequest.Builder, HttpRequest.Builder> clientModifier, final Function<RestResponse, T> responseHandler) {
        this.instance = instance;
        this.endpoint = endpoint;
        this.method = method;
        this.clientModifier = clientModifier;
        this.responseHandler = responseHandler;
    }

    public RestAction(final JavaShock instance, final String endpoint, final String method, final Function<RestResponse, T> responseHandler) {
        this(instance, endpoint, method, Function.identity(), responseHandler);
    }

    public RestAction(final JavaShock instance, final String endpoint, final String method) {
        this(instance, endpoint, method, Function.identity(), (response) -> {
            if(response.httpResponse.statusCode() != 200)
                throw new RuntimeException("Request failed with status code " + response.httpResponse.statusCode());
            return null;
        });
    }

    public HashMap<String, String> getHeaders() {
        final HashMap<String, String> map = new HashMap<>();
        map.put("accept", "application/json");
        map.put("OpenShockToken", instance.getToken());
        return map;
    }

    /**
     * Executes the request asynchronously. <br>
     * If you want to execute the request synchronously, use {@link #execute()} instead.
     *
     * @see #execute()
     * @see #queue(Consumer)
     * @see #queue(Consumer, Consumer)
     */
    public void queue() {
        queue(null, null);
    }

    /**
     * Executes the request asynchronously. <br>
     * If you want to execute the request synchronously, use {@link #execute()} instead.
     * @param success The consumer that will be called when the request was successful. Can be null.
     *
     * @see #execute()
     * @see #queue()
     * @see #queue(Consumer, Consumer)
     */
    public void queue(final Consumer<T> success) {
        queue(success, (e) -> {
            throw new RuntimeException(e);
        });
    }

    /**
     * Executes the request asynchronously. <br>
     * If you want to execute the request synchronously, use {@link #execute()} instead.
     * @param success The consumer that will be called when the request was successful. Can be null.
     * @param failure The consumer that will be called when the request failed. Can be null.
     *
     * @see #execute()
     * @see #queue()
     * @see #queue(Consumer)
     */
    public void queue(final Consumer<T> success, final Consumer<Throwable> failure) {
        CompletableFuture.runAsync(() -> {
            try {
                final T result = execute();
                if(success != null) success.accept(result);
            } catch (Throwable e) {
                if(failure != null) failure.accept(e);
            }
        }).join();
    }

    /**
     * Executes the request and returns the result. This method is blocking. <br>
     * If you want to execute the request asynchronously, use {@link #queue()} instead.
     * @return The result of the request
     */
    public T execute() {
        try {
            final HttpClient.Builder client = build();
            final HttpRequest.Builder request = buildRequest();
            final HttpResponse<String> response = sendRequest(
                    client.build(),
                    request.build(),
                    HttpResponse.BodyHandlers.ofString(),
                    3
            );
            return responseHandler.apply(new RestResponse<>(response, String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <C> HttpResponse<C> sendRequest(HttpClient client, HttpRequest request, HttpResponse.BodyHandler<C> handler, int retries) {
        try {
            return client.send(request, handler);
        } catch (Exception e) {
            if(retries > 0) {
                return sendRequest(client, request, handler, retries - 1);
            }
            throw new RuntimeException(e);
        }
    }

    protected HttpClient.Builder build() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_2);
    }

    protected String prepareBody() {
        return null;
    }

    protected HttpRequest.Builder buildRequest() {
        final String apiHost = instance.getApiHost();
        final String url = apiHost + ((!apiHost.endsWith("/") && !endpoint.startsWith("/")) ? "/" : "") + endpoint;
        final String body = prepareBody();
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        // Add body
        if(body != null && !method.equals("GET")) {
            request = request.header("Content-Type", contentType == null ? "application/json" : contentType);
            // send content length
            //request = request.header("Content-Length", String.valueOf(body.length()));
            // send raw body
            request.POST(HttpRequest.BodyPublishers.ofString(body));
        }
        // Apply headers
        getHeaders().forEach(request::header);
        if(clientModifier != null) {
            request = clientModifier.apply(request);
        }
        return request;
    }

    public record RestResponse<A>(HttpResponse<A> httpResponse, Class<A> aClass) {

        public String rawBody() {
            if(httpResponse.body() == null)
                return null;
            return String.valueOf(httpResponse.body());
        }

        public JsonElement getAsJsonElement() {
            if(httpResponse.statusCode() != 200)
                return null;
            if(httpResponse.body() == null)
                return null;
            String body = String.valueOf(httpResponse.body());
            if(body.isEmpty())
                return null;
            return JsonParser.parseString(body);
        }

        public JsonObject getAsJsonObject() {
            JsonElement element = getAsJsonElement();
            if(element == null || !element.isJsonObject())
                return new JsonObject();
            return element.getAsJsonObject();
        }

    }


}
