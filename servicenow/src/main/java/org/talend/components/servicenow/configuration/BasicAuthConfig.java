package org.talend.components.servicenow.configuration;

import lombok.Data;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import static org.talend.components.servicenow.configuration.BasicAuthConfig.NAME;

@Data
@Checkable(NAME)
@DataStore(NAME)
@GridLayout({
        @GridLayout.Row({ "url" }),
        @GridLayout.Row({ "username", "password" }),
})
@Documentation("Basic auth data store for Service Now")
public class BasicAuthConfig implements Serializable {

    public static final String NAME = "basicAuth";

    @Option
    private String url;

    @Option
    private String username;

    @Option
    @Credential
    private String password;

}
