package org.talend.components.zendesk.configuration;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore
@GridLayout({
        @GridLayout.Row({ "url" }),
        @GridLayout.Row({ "username", "password" })
})
@Documentation("Basic authentication for Zendesk API")
public class BasicAuth {

    @Option
    @Documentation("Zendesk instance url")
    private final String url;

    @Option
    @Documentation("Account username (e-mail).")
    private final String username;

    @Option
    @Credential
    @Documentation("Account password")
    private final String password;

    public String getAuthorizationHeader() {
        try {
            return "Basic " + Base64.getEncoder()
                    .encodeToString((this.getUsername() + ":" + this.getPassword()).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrlWithSlashEnding() {
        if (this.url == null) {
            return null;
        }

        String urlWithSlash = this.url;
        if (!url.endsWith("/")) {
            urlWithSlash += "/";
        }
        return urlWithSlash;
    }
}
