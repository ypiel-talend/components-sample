package org.talend.components.servicenow.source;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.servicenow.configuration.ServiceNowDataSet;
import org.talend.components.servicenow.service.ServiceNowClient;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.processor.data.ObjectMap;

public class ServiceNowSource implements Serializable {

    private final ServiceNowDataSet dataSet;

    private final ServiceNowClient service;

    public ServiceNowSource(@Option("dataSet") final ServiceNowDataSet dataSet, final ServiceNowClient service) {
        this.dataSet = dataSet;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
    }

    @Producer
    public ObjectMap next() {
        // this is the method allowing you to go through the dataset associated
        // to the component dtSet
        //
        // return null means the dataset has no more data to go through
        return null;
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
    }
}