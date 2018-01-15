package org.talend.components.servicenow.output;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.talend.components.servicenow.ServiceNow.API_URL;
import static org.talend.components.servicenow.ServiceNow.PASSWORD;
import static org.talend.components.servicenow.ServiceNow.USER;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.OutputConfig;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.processor.data.FlatObjectMap;
import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.junit.ExceptionVerifier;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;
import org.talend.sdk.component.runtime.manager.service.HttpClientFactoryImpl;
import org.talend.sdk.component.runtime.output.Processor;

public class ServiceNowOutputTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    @ClassRule
    public static final JUnit4HttpApi API = new JUnit4HttpApi().activeSsl();

    //    static {
    //        System.setProperty("talend.junit.http.capture", "true");
    //    }

    @Rule
    public final JUnit4HttpApiPerMethodConfigurator configurator = new JUnit4HttpApiPerMethodConfigurator(API);

    final BasicAuthConfig ds = new BasicAuthConfig(API_URL, USER, PASSWORD);

    @Rule
    public ExceptionVerifier<HttpException> exceptionVerifier = new ExceptionVerifier<>();

    @Test
    public void insertRecord() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Insert);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("number", "ABCDEF123");
        }};
        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(new FlatObjectMap(record)));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

    @Test
    public void updateRecord() {
        String randomNumber = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("number", randomNumber);
        }};
        TableApiClient client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
        client.base(ds.getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final ObjectMap newRec =
                client.create("incident", ds.getAuthorizationHeader(), false, new FlatObjectMap(record));
        String id = (String) newRec.get("sys_id");

        //
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Update);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        String randomNumberUpdate = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        Map<String, Object> recordUpdate = new HashMap<String, Object>() {{
            put("sys_id", id);
            put("number", randomNumberUpdate);
        }};
        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(new FlatObjectMap(recordUpdate)));
        COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

    @Test
    public void delete() {
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("number", "ABCDEFG123");
        }};
        TableApiClient client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
        client.base(ds.getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final ObjectMap newRec =
                client.create("incident", ds.getAuthorizationHeader(), false, new FlatObjectMap(record));
        String id = (String) newRec.get("sys_id");
        deleteRecordById(id);

//        exceptionVerifier.assertWith(e -> {
//            assertEquals(404, e.getResponse().status());
//            final TableApiClient.Status status =
//                    (TableApiClient.Status) e.getResponse().error(TableApiClient.Status.class);
//            assertEquals("No Record found", status.getError().getMessage());
//            assertEquals("Record doesn't exist or ACL restricts the record retrieval", status.getError().getDetail());
//        });
//        deleteRecordById(id);
    }

    @Test
    public void deleteNonExistingRecord() {
        exceptionVerifier.assertWith(e -> {
            assertEquals(404, e.getResponse().status());
            final TableApiClient.Status status =
                    (TableApiClient.Status) e.getResponse().error(TableApiClient.Status.class);
            assertEquals("No Record found", status.getError().getMessage());
            assertEquals("Record doesn't exist or ACL restricts the record retrieval", status.getError().getDetail());
        });

        deleteRecordById("NoId");
    }

    private void deleteRecordById(String id) {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Delete);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("sys_id", id);
        }};
        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(new FlatObjectMap(record)));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

}