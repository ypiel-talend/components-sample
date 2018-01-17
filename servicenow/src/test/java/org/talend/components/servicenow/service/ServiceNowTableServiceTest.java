package org.talend.components.servicenow.service;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.talend.components.servicenow.ServiceNow.API_URL;
import static org.talend.components.servicenow.ServiceNow.PASSWORD;
import static org.talend.components.servicenow.ServiceNow.USER;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.QueryBuilder;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.service.schema.Schema;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;
import org.talend.sdk.component.runtime.manager.service.HttpClientFactoryImpl;

public class ServiceNowTableServiceTest {

    @ClassRule
    public static final JUnit4HttpApi API = new JUnit4HttpApi().activeSsl();

//    static {
//        System.setProperty("talend.junit.http.capture", "true");
//    }

    @Rule
    public final JUnit4HttpApiPerMethodConfigurator configurator = new JUnit4HttpApiPerMethodConfigurator(API);

    private BasicAuthConfig dataStore = null;

    private TableApiClient client;

    @Before
    public void before() {
        dataStore = new BasicAuthConfig(API_URL, USER, PASSWORD);
        client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
    }

    @Test
    public void guessTableSchemaTest() {
        final List<String> fields = asList("number", "short_description", "due_date");
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setFields(fields);
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        configuration.setMaxRecords(10);
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final Schema schema = new ServiceNowTableService().guessTableSchema(configuration, client, null);
        assertNotNull(schema);
        assertEquals(3, schema.getEntries().size());
        schema.getEntries().forEach(e -> assertTrue(fields.contains(e.getName())));
    }

    @Test
    public void guessTableSchemaWhenNoRecordTest() {
        final List<String> fields = asList("number", "short_description", "due_date");
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        configuration.setQueryBuilder(new ArrayList<QueryBuilder>() {{
            add(new QueryBuilder(QueryBuilder.Fields.number, QueryBuilder.Operation.Equals, "ImpossibleNumber007"));
        }});
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setFields(fields);
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        configuration.setMaxRecords(10);
        client.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final Schema schema = new ServiceNowTableService().guessTableSchema(configuration, client, null);
        assertNotNull(schema);
        assertTrue(schema.getEntries().isEmpty());
    }
}
