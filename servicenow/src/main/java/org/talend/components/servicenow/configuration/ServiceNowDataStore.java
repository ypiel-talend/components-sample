package org.talend.components.servicenow.configuration;

import lombok.Data;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;

@Data
@org.talend.sdk.component.api.configuration.type.DataStore
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "url" }),
        @GridLayout.Row({ "username" }),
        @GridLayout.Row({ "password" })
})
@OptionsOrder({ "url", "username", "password" })
public class ServiceNowDataStore {

    @Option
    private String url;

    @Option
    private String username;

    @Credential
    @Option
    private String password;

}
