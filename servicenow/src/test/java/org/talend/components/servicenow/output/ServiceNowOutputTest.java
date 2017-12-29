package org.talend.components.servicenow.output;

import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import io.specto.hoverfly.junit.core.HoverflyConfig;

import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.ApiSimulationRule;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.processor.data.FlatObjectMap;
import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.junit.ExceptionVerifier;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.manager.service.HttpClientFactoryImpl;
import org.talend.sdk.component.runtime.output.Processor;

public class ServiceNowOutputTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    @Rule
    public ApiSimulationRule apiSimulationRule =
            new ApiSimulationRule(SIMULATE, HoverflyConfig.configs());

    @Rule
    public ExceptionVerifier<HttpException> exceptionVerifier = new ExceptionVerifier<>();

    @Test
    public void insertRecord() {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com/", "admin", "pass"));
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
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com/", "user", "pass"));
        configuration.setActionOnTable(OutputConfig.ActionOnTable.Update);
        configuration.setNoResponseBody(false);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        final Processor processor = COMPONENT_FACTORY.createProcessor(ServiceNowOutput.class, configuration);
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("sys_id", "0babea89db3a03002b54771c8c96196b");
            put("number", "AZERTY12345");
        }};
        final JoinInputFactory joinInputFactory = new JoinInputFactory()
                .withInput("__default__", singletonList(new FlatObjectMap(record)));
        final SimpleComponentRule.Outputs outputs = COMPONENT_FACTORY.collect(processor, joinInputFactory);
    }

    @Test
    public void delete() {
        final BasicAuthConfig ds = new BasicAuthConfig("https://dev44668.service-now.com/", "user", "pass");
        Map<String, Object> record = new HashMap<String, Object>() {{
            put("number", "ABCDEFG123");
        }};

        TableApiClient client = new HttpClientFactoryImpl("test").create(TableApiClient.class, null);
        client.base(ds.getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        final ObjectMap newRec =
                client.create("incident", ds.getAuthorizationHeader(), false, new FlatObjectMap(record));
        String id = (String) newRec.get("sys_id");
        deleteRecordById(id);
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
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com/", "user", "pass"));
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