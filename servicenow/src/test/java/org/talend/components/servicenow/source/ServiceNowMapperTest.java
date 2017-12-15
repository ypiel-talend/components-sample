package org.talend.components.servicenow.source;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static io.specto.hoverfly.junit.core.SimulationSource.defaultPath;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.components.servicenow.configuration.TableDataSet.READ_ALL_RECORD_FROM_SERVER;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import io.specto.hoverfly.junit.core.Hoverfly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.OrderBuilder;
import org.talend.components.servicenow.configuration.QueryBuilder;
import org.talend.components.servicenow.configuration.TableAPIConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.input.Mapper;

public class ServiceNowMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    //    @ClassRule
    //    public static HoverflyRule hoverflyRule = HoverflyRule
    //            .inCaptureMode("C:\\Users\\akhabali\\Documents\\hoverfly\\incidents_WithInvalidQuery.json");

    @Test
    public void produceWithConcurrency() throws IOException {
        try (Hoverfly hoverfly = new Hoverfly(configs(), SIMULATE)) {
            hoverfly.start();
            hoverfly.simulate(defaultPath("incidents_1000.json"));

            BasicAuthConfig dataStore = new BasicAuthConfig();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final TableDataSet configuration = new TableDataSet();
            configuration.setDataStore(dataStore);
            final TableAPIConfig apiConfig = new TableAPIConfig();
            apiConfig.setTableName(TableAPIConfig.Tables.incident);
            configuration.setTableAPIConfig(apiConfig);
            configuration.setMaxRecords(1000);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager()
                    .findMapper("ServiceNow", "ServiceNowInput", 1,
                            configurationByExample(configuration, "tableDataSet"))
                    .orElseThrow(() -> new RuntimeException("fail"));
            final List<TableRecord> serviceNowRecords = COMPONENT_FACTORY.collect(TableRecord.class, mapper, 2000, 24)
                    .collect(toList());
            assertEquals(1000, serviceNowRecords.size());
            assertNotNull(serviceNowRecords.get(0).getData().get("number"));
        }
    }

    @Test
    public void produceWithQuery() throws IOException {

        try (Hoverfly hoverfly = new Hoverfly(configs(), SIMULATE)) {
            hoverfly.start();
            hoverfly.simulate(defaultPath("incidents_withQuery.json"));

            BasicAuthConfig dataStore = new BasicAuthConfig();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final TableDataSet configuration = new TableDataSet();
            configuration.setDataStore(dataStore);
            final TableAPIConfig apiConfig = new TableAPIConfig();
            apiConfig.setTableName(TableAPIConfig.Tables.incident);
            configuration.setTableAPIConfig(apiConfig);
            configuration.setQueryBuilder(new ArrayList<QueryBuilder>() {{
                add(new QueryBuilder(QueryBuilder.Fields.active, QueryBuilder.Operation.Equals, "true"));
            }});
            configuration.setOrdered(true);
            configuration.setOrder(new ArrayList<OrderBuilder>() {{
                add(new OrderBuilder(QueryBuilder.Fields.number, OrderBuilder.Order.ASC));
                add(new OrderBuilder(QueryBuilder.Fields.category, OrderBuilder.Order.DESC));
            }});
            configuration.setMaxRecords(100);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                    configurationByExample(configuration, "tableDataSet"))
                    .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));

            final List<TableRecord> serviceNowRecords = COMPONENT_FACTORY.collect(TableRecord.class, mapper, 1000, 2)
                    .collect(toList());
            assertEquals(100, serviceNowRecords.size());
            assertNotNull(serviceNowRecords.get(0).getData().get("number"));
        }
    }

    @Test
    public void produceWithReadFullDataSet() throws IOException {

        try (Hoverfly hoverfly = new Hoverfly(configs(), SIMULATE)) {
            hoverfly.start();
            hoverfly.simulate(defaultPath("incidents_WithReadFullDataSet.json"));

            //test
            BasicAuthConfig dataStore = new BasicAuthConfig();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final TableAPIConfig apiConfig = new TableAPIConfig();
            apiConfig.setTableName(TableAPIConfig.Tables.incident);
            final TableDataSet configuration = new TableDataSet();
            configuration.setDataStore(dataStore);
            configuration.setTableAPIConfig(apiConfig);
            configuration.setQueryBuilder(new ArrayList<QueryBuilder>() {{
                add(new QueryBuilder(QueryBuilder.Fields.active, QueryBuilder.Operation.Equals, "true"));
                add(new QueryBuilder(QueryBuilder.Fields.priority, QueryBuilder.Operation.Equals, "1"));
            }});
            configuration.setOrdered(true);
            configuration.setOrder(new ArrayList<OrderBuilder>() {{
                add(new OrderBuilder(QueryBuilder.Fields.number, OrderBuilder.Order.ASC));
                add(new OrderBuilder(QueryBuilder.Fields.category, OrderBuilder.Order.DESC));
            }});
            configuration.setMaxRecords(READ_ALL_RECORD_FROM_SERVER);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                    configurationByExample(configuration, "tableDataSet"))
                    .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));

            final List<TableRecord> serviceNowRecords = COMPONENT_FACTORY.collect(TableRecord.class, mapper, 1000, 2)
                    .collect(toList());
            assertEquals(1000, serviceNowRecords.size());
            assertNotNull(serviceNowRecords.get(0).getData().get("number"));
        }
    }
}