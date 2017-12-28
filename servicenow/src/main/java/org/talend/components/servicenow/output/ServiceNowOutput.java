package org.talend.components.servicenow.output;

import static java.util.stream.Collectors.joining;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.processor.data.ObjectMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    @PostConstruct
    public void init() {
        client.base(outputConfig.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
    }

    @ElementListener
    public void onNext(@Input final ObjectMap record) {

        switch (outputConfig.getActionOnTable()) {
        case Insert:
            final ObjectMap newRec =
                    client.create(outputConfig.getCommonConfig().getTableName().name(),
                            outputConfig.getDataStore().getAuthorizationHeader(),
                            outputConfig.isNoResponseBody(),
                            record);

            if (!outputConfig.isNoResponseBody() && newRec != null) {
                log.info(newRec.keys().stream()
                        .map(k -> k + ":" + newRec.get(k))
                        .collect(joining(";")));
            }
            break;
        default:
            throw new UnsupportedOperationException(outputConfig.getActionOnTable() + " is not supported yet");
        }

    }
}