package org.talend.components.widget.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;

@OptionsOrder({"url","user","password"})
@DataStore("Connection")
public class ConnectionConfiguration implements Serializable {

    @Option
    private String url;

    @Option
    private String user;

    @Option
    @Credential
    private String password;

    public String getURL() {
        return url;
    }

    public ConnectionConfiguration setURL(String URL) {
        this.url = URL;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ConnectionConfiguration setUser(String User) {
        this.user = User;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ConnectionConfiguration setPassword(String Password) {
        this.password = Password;
        return this;
    }
}