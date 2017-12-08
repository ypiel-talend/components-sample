package org.talend.components.servicenow.output;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.ServiceNowRestClient;
import org.talend.components.servicenow.service.ServiceNowRestClientBuilder;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "ServiceNowOutput")
@Processor(name = "ServiceNowOutput")
@Documentation("ServiceNowOutput is a configurable connector able to write records to Service Now Table")
public class ServiceNowOutput implements Serializable {

    private final OutputConfig outputConfig;

    private final Messages i18n;

    private ServiceNowRestClient client;

    public ServiceNowOutput(@Option("configuration") final OutputConfig outputConfig,
            final Messages i18n) {
        this.outputConfig = outputConfig;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        client = new ServiceNowRestClientBuilder(outputConfig.getDataStore(), i18n).clientV2();
    }

    @ElementListener
    public void onNext(@Input final TableRecord record) {

        switch (outputConfig.getActionOnTable()) {
        case Insert:
            client.table().createRecord(outputConfig, record);
            break;
        default:
            throw new UnsupportedOperationException(outputConfig.getActionOnTable() + " is not supported yet");
        }

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