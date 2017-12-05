package org.talend.components.servicenow.output;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.service.ServiceNowRestClient;
import org.talend.components.servicenow.service.ServiceNowRestClientBuilder;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;

// default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Version(2)
@Icon(value = Icon.IconType.CUSTOM, custom = "ServiceNowOutput")
// you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename_icon32.png in resources
@Processor(name = "ServiceNowOutput")
public class ServiceNowOutput implements Serializable {

    private final OutputConfig outputConfig;

    private ServiceNowRestClient client;

    public ServiceNowOutput(@Option("configuration") final OutputConfig outputConfig) {
        this.outputConfig = outputConfig;
    }

    @PostConstruct
    public void init() {
        client = new ServiceNowRestClientBuilder(outputConfig.getDataStore()).clientV2();
    }

    @ElementListener
    public void onNext(@Input final TableRecord record) {
        client.table().createRecord(outputConfig, record);
    }

    @PreDestroy
    public void release() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                //no-op
            }
        }
    }
}