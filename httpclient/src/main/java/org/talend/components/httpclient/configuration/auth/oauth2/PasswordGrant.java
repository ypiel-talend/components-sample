package org.talend.components.httpclient.configuration.auth.oauth2;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "accessTokeUrl" }),
        @GridLayout.Row({ "username" }),
        @GridLayout.Row({ "password" }),
        @GridLayout.Row({ "clientId" }),
        @GridLayout.Row({ "clientSecret" }),
        @GridLayout.Row({ "scopes" }),
})
public class PasswordGrant {

    @Option
    @Pattern("^(http|https)://")
    @Required
    @Documentation("")
    private final String accessTokeUrl;

    @Option
    @Required
    @Documentation("")
    private final String username;

    @Option
    @Required
    @Credential
    @Documentation("")
    private final String password;

    @Option
    @Required
    @Documentation("")
    private final String clientId;

    @Option
    @Required
    @Credential
    @Documentation("")
    private final String clientSecret;

    @Option
    @Documentation("")
    private String scopes;

}
