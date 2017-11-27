package org.talend.components.widget.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

@GridLayout({
    @GridLayout.Row({ "URL" }),
    @GridLayout.Row({ "User" }),
    @GridLayout.Row({ "Password" })
})
public class ConnectionConfiguration implements Serializable {

    @Option
    private String URL;

    @Option
    private String User;

    @Option
    private String Password;

    public String getURL() {
        return URL;
    }

    public ConnectionConfiguration setURL(String URL) {
        this.URL = URL;
        return this;
    }

    public String getUser() {
        return User;
    }

    public ConnectionConfiguration setUser(String User) {
        this.User = User;
        return this;
    }

    public String getPassword() {
        return Password;
    }

    public ConnectionConfiguration setPassword(String Password) {
        this.Password = Password;
        return this;
    }
}