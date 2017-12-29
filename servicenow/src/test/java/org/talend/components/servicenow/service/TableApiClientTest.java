package org.talend.components.servicenow.service;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static org.junit.Assert.assertEquals;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.talend.components.servicenow.ApiSimulationRule;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.junit.ExceptionVerifier;
import org.talend.sdk.component.runtime.manager.service.HttpClientFactoryImpl;

public class TableApiClientTest {

    @Rule
    public TestName testName = new TestName();

    @Rule
    public ExceptionVerifier<HttpException> exceptionVerifier = new ExceptionVerifier<>();

    @Rule
    public ApiSimulationRule apiSimulationRule = new ApiSimulationRule(SIMULATE, configs());

    @Test
    public void healthCheckInvalidCredentials() {

        exceptionVerifier.assertWith(e -> {
            assertEquals(401, e.getResponse().status());
            final TableApiClient.Status status =
                    (TableApiClient.Status) e.getResponse().error(TableApiClient.Status.class);
            assertEquals("User Not Authenticated", status.getError().getMessage());
            assertEquals("Required to provide Auth information", status.getError().getDetail());
        });

        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com", "badUser", "badPassword"));
        TableApiClient client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        client.healthCheck(configuration.getDataStore().getAuthorizationHeader());
    }

    @Test
    public void healthCheckOk() {
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com", "goodUser", "goodPasswd"));
        TableApiClient client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        client.healthCheck(configuration.getDataStore().getAuthorizationHeader());
    }

}
