package org.talend.components.servicenow.output;

import java.io.Serializable;

import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.http.TableApiClient;
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

    private TableApiClient client;

    public ServiceNowOutput(@Option("configuration") final OutputConfig outputConfig,
            final Messages i18n, TableApiClient client) {
        this.outputConfig = outputConfig;
        this.i18n = i18n;
        this.client = client;
    }

    @ElementListener
    public void onNext(@Input final TableRecord record) {

        switch (outputConfig.getActionOnTable()) {
        case Insert:
            client.create(outputConfig.getTableAPIConfig().getTableName().name(),
                    outputConfig.getDataStore().getAuthorizationHeader(), outputConfig.isNoResponseBody(), true,
                    record);
            break;
        default:
            throw new UnsupportedOperationException(outputConfig.getActionOnTable() + " is not supported yet");
        }

    }
}