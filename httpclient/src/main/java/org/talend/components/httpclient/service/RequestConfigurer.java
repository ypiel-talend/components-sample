package org.talend.components.httpclient.service;

import javax.json.JsonObject;

import org.talend.components.httpclient.configuration.RequestConfig;
import org.talend.components.httpclient.configuration.auth.oauth2.Oauth2;
import org.talend.components.httpclient.configuration.auth.oauth2.PasswordGrant;
import org.talend.sdk.component.api.service.http.Configurer;
import org.talend.sdk.component.api.service.http.Response;

public class RequestConfigurer implements Configurer {

    @Override
    public void configure(final Connection connection, final ConfigurerConfiguration configuration) {
        final RequestConfig config = configuration.get("configuration", RequestConfig.class);
        if (config.getConnectionTimeout() != null) {
            connection.withConnectionTimeout(config.getConnectionTimeout());
        }
        if (config.getReadTimeout() != null) {
            connection.withReadTimeout(config.getReadTimeout());
        }

        switch (config.getAuthentication().getType()) {
        case Basic:
            connection.withHeader("Authorization", config.getAuthentication().getBasic().getAuthorizationHeader());
            break;
        case Bearer:
            connection.withHeader("Authorization", "Bearer " + config.getAuthentication().getBearerToken());
            break;
        case Oauth2:
            final Oauth2 oauth2 = config.getAuthentication().getOauth2();
            final Client httpclient = configuration.get("httpClient", Client.class);
            final String accessToken = getAccessToken(httpclient, oauth2);
            switch (oauth2.getAuthMethod()) {
            case Url:
                //FIXME : need to extend the api to handle this case withQueryParam();
                break;
            case Header:
            default:
                connection.withHeader("Authorization", "Bearer " + accessToken);
                break;
            }
            break;
        case NoAuth:
        default:
            break;
        }

    }

    private String getAccessToken(final Client httpclient, final Oauth2 oauth2) {
        switch (oauth2.getGrantType()) {
        case Password:
        default:
            final PasswordGrant passwordGrant = oauth2.getPasswordGrant();
            final Response<JsonObject> tokenResponse = httpclient.token(passwordGrant.getAccessTokeUrl(),
                    "password",
                    passwordGrant.getUsername(),
                    passwordGrant.getPassword(),
                    passwordGrant.getClientId(),
                    passwordGrant.getClientSecret(),
                    passwordGrant.getScopes().isEmpty() ? null : passwordGrant.getScopes());
            if (tokenResponse.status() >= 400) {
                throw new IllegalStateException(
                        "Http Error (" + tokenResponse.status() + ")\n" + tokenResponse.error(String.class));
            }
            return tokenResponse.body().getString("access_token");
        }
    }
}
