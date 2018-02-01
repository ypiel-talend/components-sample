package org.talend.components.servicenow.output;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.talend.components.servicenow.ServiceNow.API_URL;
import static org.talend.components.servicenow.ServiceNow.PASSWORD;
import static org.talend.components.servicenow.ServiceNow.USER;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.OutputConfig;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.junit.ExceptionVerifier;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;
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
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        JsonObject record = Json.createObjectBuilder().add("number", "ABCDEF123").build();

        final JoinInputFactory joinInputFactory =
                new JoinInputFactory().withInput("__default__", singletonList(record));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
        final List<JsonObject> insertedRecords = outputs.get(JsonObject.class, "__default__");
        assertEquals(1, insertedRecords.size());
        assertNotNull(insertedRecords.get(0).get("sys_id"));
        assertEquals("ABCDEF123", insertedRecords.get(0).getString("number"));
    }

    @Test
    public void insertRecordNoResponse() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Insert);
        configuration.setNoResponseBody(true);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        JsonObject record = Json.createObjectBuilder().add("number", "ABCDEF123").build();

        final JoinInputFactory joinInputFactory =
                new JoinInputFactory().withInput("__default__", singletonList(record));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
        final List<JsonObject> insertedRecords = outputs.get(JsonObject.class, "__default__");
        assertNull(insertedRecords);
    }

    @Test
    public void insertRecordError() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Insert);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        JsonObject record = Json.createObjectBuilder().add("number", "GFHFGHTNBGF").build();
        JsonObject recordTobeRejected = Json.createObjectBuilder()
                .add("sys_id", "00aaae01db4013004a4576efbf96197f")
                .add("number", "2135483521432").build();

        final JoinInputFactory joinInputFactory =
                new JoinInputFactory().withInput("__default__", asList(record, recordTobeRejected));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);

        final List<JsonObject> insertedRecords = outputs.get(JsonObject.class, "__default__");
        assertEquals(1, insertedRecords.size());
        assertNotNull(insertedRecords.get(0).get("sys_id"));
        assertEquals("GFHFGHTNBGF", insertedRecords.get(0).getString("number"));

        final List<Reject> rejectedRecords = outputs.get(Reject.class, "reject");
        assertEquals(1, rejectedRecords.size());
        assertEquals("Operation Failed", rejectedRecords.get(0).getErrorMessage());
        assertEquals("Error during insert of incident (2135483521432)", rejectedRecords.get(0).getErrorDetail());
        assertEquals("2135483521432", rejectedRecords.get(0).getRecord().getString("number"));
    }

    @Test
    public void updateRecord() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Update);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);

        String randomNumber = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        JsonObject record = Json.createObjectBuilder()
                .add("number", randomNumber).build();

        TableApiClient client = COMPONENT_FACTORY.findService(TableApiClient.class);
        client.base(ds.getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final JsonObject newRec =
                client.create("incident", ds.getAuthorizationHeader(), false, record);
        String id = newRec.getString("sys_id");

        String randomNumberUpdate = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        JsonObject recordUpdate = Json.createObjectBuilder()
                .add("sys_id", id)
                .add("number", randomNumberUpdate).build();

        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(recordUpdate));
        COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

    @Test
    public void delete() {

        JsonObject record = Json.createObjectBuilder()
                .add("number", "ABCDEFG123")
                .build();

        TableApiClient client = COMPONENT_FACTORY.findService(TableApiClient.class);
        client.base(ds.getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final JsonObject newRec = client.create("incident", ds.getAuthorizationHeader(), false, record);
        String id = (String) newRec.getString("sys_id");
        deleteRecordById(id);
    }

    @Test
    public void deleteNonExistingRecord() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(ds);
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Delete);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        JsonObject record = Json.createObjectBuilder()
                .add("sys_id", "NoId").build();

        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(record));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
        assertEquals(1, outputs.size());
        final List<Reject> rejects = outputs.get(Reject.class, "reject");
        assertEquals(1, rejects.size());
        assertEquals(404, rejects.get(0).getCode());
        assertEquals("No Record found", rejects.get(0).getErrorMessage());
        assertEquals("Record doesn't exist or ACL restricts the record retrieval", rejects.get(0).getErrorDetail());
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
        JsonObject record = Json.createObjectBuilder()
                .add("sys_id", id).build();
        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(record));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

}