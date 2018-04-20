package org.talend.components.httpclient.configuration.auth;

import static org.talend.components.httpclient.service.ValidationService.URL;

import java.io.Serializable;

import org.talend.components.httpclient.configuration.HttpMethod;
import org.talend.components.httpclient.configuration.auth.oauth2.Oauth2;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore
@GridLayout({
        @GridLayout.Row({ "methodType" }),
        @GridLayout.Row({ "url" }),
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "type" }),
        @GridLayout.Row({ "basic" }),
        @GridLayout.Row({ "bearerToken" }),
        @GridLayout.Row({ "oauth2" }),
})
@Documentation("Http authentication data store")
public class Authentication implements Serializable {

    @Option
    @Required
    @DefaultValue("GET")
    @Documentation("")
    private HttpMethod methodType;

    @Option
    @Required
    @Validable(URL)
    @Documentation("http request url")
    private String url;

    @Option
    @Required
    @Documentation("")
    private Authorization.AuthorizationType type = Authorization.AuthorizationType.NoAuth;

    @Option
    @ActiveIf(target = "type", value = "Basic")
    @Documentation("")
    private Basic basic;

    @Option
    @Credential
    @ActiveIf(target = "type", value = "Bearer")
    @Documentation("")
    private String bearerToken;

    @Option
    @ActiveIf(target = "type", value = "Oauth2")
    @Documentation("")
    private Oauth2 oauth2;

}
