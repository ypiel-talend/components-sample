package org.talend.components.demo.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore
@Checkable
@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "url" }),
        @GridLayout.Row({ "username", "password" }),
})
@Documentation("TODO fill the documentation for this configuration")
public class AuthConfiguration implements Serializable {

    @Validable("validateURL")
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String url;

    @Pattern("/^[a-zA-Z0-9]+$/")
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String username;

    @Credential
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String password;

    public String getUrl() {
        return url;
    }

    public AuthConfiguration setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AuthConfiguration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthConfiguration setPassword(String password) {
        this.password = password;
        return this;
    }
}