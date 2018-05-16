package com.talend.components.source;

import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.json.JsonBuilderFactory;

import com.talend.components.service.TTacokitRestClient;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

import com.talend.components.service.Tcomp_componentService;

//
// this class role is to enable the work to be distributed in environments supporting it.
//
@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = Icon.IconType.CUSTOM, custom = "tacokit_list") // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename_icon32.png in resources
@PartitionMapper(name = "ComponentList")
@Documentation("TODO fill the documentation for this mapper")
public class ComponentListMapper implements Serializable {
    private final ComponentListMapperConfiguration configuration;
    private final Tcomp_componentService service;
    private final JsonBuilderFactory jsonBuilderFactory;
    private final TTacokitRestClient apiClient;

    public ComponentListMapper(@Option("configuration") final ComponentListMapperConfiguration configuration,
                        final Tcomp_componentService service,
                        final JsonBuilderFactory jsonBuilderFactory,
                        final TTacokitRestClient apiClient) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
        this.apiClient = apiClient;
    }

    @PostConstruct
    public void init(){
        apiClient.base(configuration.getDataStore().getUrl());
    }

    @Assessor
    public long estimateSize() {
        // this method should return the estimation of the dataset size
        // it is recommended to return a byte value
        // if you don't have the exact size you can use a rough estimation
        return 1L;
    }

    @Split
    public List<ComponentListMapper> split(@PartitionSize final long bundles) {
        // overall idea here is to split the work related to configuration in bundles of size "bundles"
        //
        // for instance if your estimateSize() returned 1000 and you can run on 10 nodes
        // then the environment can decide to run it concurrently (10 * 100).
        // In this case bundles = 100 and we must try to return 10 ComponentListMapper with 1/10 of the overall work each.
        //
        // default implementation returns this which means it doesn't support the work to be split
        return singletonList(this);
    }

    @Emitter
    public ComponentListSource createWorker() {
        // here we create an actual worker,
        // you are free to rework the configuration etc but our default generated implementation
        // propagates the partition mapper entries.
        return new ComponentListSource(configuration, service, jsonBuilderFactory, apiClient);
    }
}