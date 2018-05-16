package com.talend.components.source;

import java.io.Serializable;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.Json;

import com.talend.components.service.TTacokitRestClient;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import com.talend.components.service.Tcomp_componentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Documentation("TODO fill the documentation for this source")
public class ComponentListSource implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(ComponentListSource.class);

    private final ComponentListMapperConfiguration configuration;

    private final Tcomp_componentService service;

    private final JsonBuilderFactory jsonBuilderFactory;

    private final TTacokitRestClient apiClient;

    private Iterator<JsonValue> components;

    public ComponentListSource(
            @Option("configuration")
            final ComponentListMapperConfiguration configuration, final Tcomp_componentService service,
            final JsonBuilderFactory jsonBuilderFactory, final TTacokitRestClient apiClient) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
        this.apiClient = apiClient;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance

        logger.debug("PostConstruct : retrieve components and createthe iterator.");
        components = apiClient.listComponents().body().getJsonArray("components").iterator();
    }

    @Producer
    public JsonObject next() {
        // this is the method allowing you to go through the dataset associated
        // to the component configuration
        //
        // return null means the dataset has no more data to go through
        // you can use the jsonBuilderFactory to create new JsonObjects.
        logger.debug("Producer: next compo.");

        JsonObject jsonO = Json.createObjectBuilder().build();
        boolean match = false;
        while (jsonO != null && !match) {
            jsonO = _next();

            if (jsonO != null) {
                String family = jsonO.get("familyDisplayName").toString();
                String filterFamily = this.configuration.getFilter_family().trim();
                if (!filterFamily.isEmpty()) {
                    match = family.contains(filterFamily);
                } else {
                    match = true;
                }

                String name = jsonO.get("displayName").toString();
                String filterName = this.configuration.getFilter_name().trim();
                if (!filterName.isEmpty()) {
                    match = match && name.contains(filterName);
                } else {
                    match = match && true;
                }

            }
        }

        return jsonO;
    }

    private JsonObject _next() {
        return (components.hasNext()) ? components.next().asJsonObject() : null;
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached

        logger.debug("PreDestroy: nothing to release.");
    }
}