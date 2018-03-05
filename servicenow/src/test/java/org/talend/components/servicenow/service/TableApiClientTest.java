package org.talend.components.servicenow.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.talend.components.servicenow.ServiceNow.API_URL;
import static org.talend.components.servicenow.ServiceNow.PASSWORD;
import static org.talend.components.servicenow.ServiceNow.USER;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.ServiceNow;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.junit.ExceptionVerifier;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;

public class TableApiClientTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    @ClassRule
    public static final JUnit4HttpApi API = new JUnit4HttpApi().activeSsl();

    //            static {
    //                System.setProperty("talend.junit.http.capture", "true");
    //            }

    @Rule
    public final JUnit4HttpApiPerMethodConfigurator configurator = new JUnit4HttpApiPerMethodConfigurator(API);

    @Rule
    public ExceptionVerifier<HttpException> exceptionVerifier = new ExceptionVerifier<>();

    @Rule
    public ExceptionVerifier<RuntimeException> runtimeExceptionVerifier = new ExceptionVerifier<>();

    private TableApiClient client;

    @Before
    public void init() {
        client = COMPONENT_FACTORY.findService(TableApiClient.class);
    }

    @Test
    public void healthCheckInvalidCredentials() {

        exceptionVerifier.assertWith(e -> {
            assertEquals(401, e.getResponse().status());
            final JsonObject status = (JsonObject) e.getResponse().error(JsonObject.class);
            assertEquals("User Not Authenticated", status.getJsonObject("error").getString("message"));
            assertEquals("Required to provide Auth information", status.getJsonObject("error").getString("detail"));
        });

        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(new BasicAuthConfig(ServiceNow.API_URL, "badUser", "badPassword"));
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        client.healthCheck(configuration.getDataStore().getAuthorizationHeader());
    }

    @Test
    public void healthCheckOk() {
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(new BasicAuthConfig(API_URL, USER, PASSWORD));
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        client.healthCheck(configuration.getDataStore().getAuthorizationHeader());
    }

    @Test
    @Ignore("capture response when instance is hibernating.")
    public void unsupportedResponseType() {
        runtimeExceptionVerifier.assertWith(e -> {
            assertTrue(RuntimeException.class.isInstance(e));
            assertEquals(
                    "ServiceNow instance is down or hibernating. Please check that your instance is up and running !",
                    e.getMessage());
        });
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(new BasicAuthConfig(API_URL, USER, PASSWORD));
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        client.healthCheck(configuration.getDataStore().getAuthorizationHeader());
    }

}
