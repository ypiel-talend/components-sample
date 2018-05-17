package com.talend.components.source.details;

import java.io.Serializable;
import java.net.HttpURLConnection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonObject;

import com.talend.components.service.TTacokitRestClient;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;

import com.talend.components.service.Tcomp_componentService;
import org.talend.sdk.component.api.service.http.Response;

@Version(1)
// default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "tacokit_details")
// you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename_icon32.png in resources
@Processor(name = "ComponentDetails")
@Documentation("TODO fill the documentation for this processor")
public class ComponentDetailsProcessor implements Serializable {

    private final ComponentDetailsProcessorConfiguration configuration;

    private final Tcomp_componentService service;

    private final TTacokitRestClient apiClient;

    public ComponentDetailsProcessor(
            @Option("configuration")
            final ComponentDetailsProcessorConfiguration configuration,
            final Tcomp_componentService service,
            final TTacokitRestClient apiClient) {
        this.configuration = configuration;
        this.service = service;
        this.apiClient = apiClient;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
        apiClient.base(configuration.getDataStore().getUrl());
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input
            final JsonObject defaultInput,
            @Output
            final OutputEmitter<JsonObject> defaultOutput,
            @Output("REJECT")
            final OutputEmitter<ComponentDetailsREJECTOutput> REJECTOutput) {

        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).
        String componentId = defaultInput.get("id").asJsonObject().getString("id");
        Response<JsonObject> response = apiClient.getComponentDetails(componentId);

        // If it fails
        if(response.status() != HttpURLConnection.HTTP_OK){
            String error = response.error(String.class);
            ComponentDetailsREJECTOutput reject = new ComponentDetailsREJECTOutput() //
                                                            .setId(componentId) //
                                                            .setHttpError(response.status()) //
                                                            .setMessage(error);
            REJECTOutput.emit(reject);
            return;
        }

        // If success
        JsonObject details = response.body().getJsonArray("details").getJsonObject(0);
        defaultOutput.emit(details);
    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }
}