package org.talend.components.demo.source;

import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.components.demo.service.Demo_componentService;
import org.talend.components.demo.service.RestClient;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

@Documentation("TODO fill the documentation for this source")
public class TripsSource implements Serializable {

    private final TripsMapperConfiguration configuration;

    private final Demo_componentService service;

    private final JsonBuilderFactory jsonBuilderFactory;

    private final RestClient restClient;

    private Iterator<JsonValue> trips;

    public TripsSource(@Option("configuration") final TripsMapperConfiguration configuration,
            final Demo_componentService service,
            final JsonBuilderFactory jsonBuilderFactory, final RestClient restClient) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
        this.restClient = restClient;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        restClient.base(configuration.getAuth().getUrl());
        JsonObject result = restClient.query(configuration.getDistanceMin(), configuration.getDistanceMax(), 10);
        trips = result.getJsonArray("trips").iterator();
    }

    @Producer
    public JsonObject next() {
        // this is the method allowing you to go through the dataset associated
        // to the component configuration
        //
        // return null means the dataset has no more data to go through
        // you can use the jsonBuilderFactory to create new JsonObjects.
        return trips.hasNext() ? trips.next().asJsonObject() : null;
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
    }
}