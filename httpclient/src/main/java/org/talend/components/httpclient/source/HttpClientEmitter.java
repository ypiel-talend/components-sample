package org.talend.components.httpclient.source;

import static java.util.Optional.ofNullable;
import static java.util.stream.Stream.of;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.talend.components.httpclient.configuration.RequestConfig;
import org.talend.components.httpclient.service.Client;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.http.Response;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "Http")
@Emitter(name = "Input")
@Documentation("")
public class HttpClientEmitter implements Serializable {

    private final RequestConfig request;

    private final Client httpClient;

    private final JsonBuilderFactory jsonBuilderFactory;

    private Iterator<JsonObject> it;

    public HttpClientEmitter(@Option("configuration") final RequestConfig request, final Client httpClient,
            final JsonBuilderFactory jsonBuilderFactory) {
        this.request = request;
        this.httpClient = httpClient;
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @PostConstruct
    public void init() {
        final JsonPointer pointer = Json.createPointer(ofNullable(request.getExtractor()).orElse(""));
        it = flatMap(doRequest(), jsonBuilderFactory)
                .map(pointer::getValue)
                .flatMap(v -> flatMap(v, jsonBuilderFactory))
                .iterator();
    }

    private JsonValue doRequest() {
        final Response<JsonValue> response = httpClient.execute(request,
                httpClient,
                request.getAuthentication().getMethodType().name(),
                request.getAuthentication().getUrl(),
                request.headers(),
                request.queryParams(),
                request.hasPayLoad() ? request.getBody() : null);

        if (response.status() >= 400) {//todo handle redirect ?
            throw new RuntimeException("Http ERROR (" + response.status() + ")\n"
                    + response.error(String.class));
        }

        return response.body();
    }

    private Stream<JsonObject> flatMap(final JsonValue jsonValue, final JsonBuilderFactory jsonBuilderFactory) {
        switch (jsonValue.getValueType()) {
        case NULL:
            return Stream.empty();
        case ARRAY:
            return jsonValue.asJsonArray()
                    .stream()
                    .map(JsonValue::asJsonObject);
        case OBJECT:
            return of(jsonValue.asJsonObject());
        case NUMBER:
            return of(jsonBuilderFactory.createObjectBuilder()
                    .add("number", JsonNumber.class.cast(jsonValue))
                    .build());
        case TRUE:
        case FALSE:
        case STRING:
        default:
            return of(jsonBuilderFactory.createObjectBuilder()
                    .add("string", JsonString.class.cast(jsonValue))
                    .build());

        }
    }

    @Producer
    public JsonObject next() {
        return it.hasNext() ? it.next() : null;
    }
}
