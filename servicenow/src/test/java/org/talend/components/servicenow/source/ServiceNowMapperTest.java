package org.talend.components.servicenow.source;

import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.components.servicenow.configuration.TableDataSet.READ_ALL_RECORD_FROM_SERVER;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import io.specto.hoverfly.junit.core.HoverflyConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.ApiSimulationRule;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.OrderBuilder;
import org.talend.components.servicenow.configuration.QueryBuilder;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.input.Mapper;

public class ServiceNowMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    @Rule
    public ApiSimulationRule apiSimulationRule = new ApiSimulationRule(SIMULATE, HoverflyConfig.configs());

    private BasicAuthConfig dataStore;

    @Before
    public void before() {
        dataStore = new BasicAuthConfig("https://dev44668.service-now.com", "fakeUser", "fakePassword");
    }

    @Test
    public void produceWithConcurrency() throws IOException {
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        configuration.setMaxRecords(100);

        // We create the component mapper instance using the configuration filled above
        final Mapper mapper = COMPONENT_FACTORY.asManager()
                .findMapper("ServiceNow", "ServiceNowInput", 1,
                        configurationByExample(configuration, "tableDataSet"))
                .orElseThrow(() -> new RuntimeException("fail"));

        final List<ObjectMap> serviceNowRecords = COMPONENT_FACTORY.collect(ObjectMap.class, mapper, 2000, 8)
                .collect(toList());
        assertEquals(configuration.getMaxRecords(), serviceNowRecords.size());
        assertNotNull(serviceNowRecords.get(0).get("number"));

    }

    @Test
    public void produceWithOrderedQuery() throws IOException {
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
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

        final List<ObjectMap> serviceNowRecords = COMPONENT_FACTORY.collect(ObjectMap.class, mapper, 1000, 2)
                .collect(toList());
        assertEquals(configuration.getMaxRecords(), serviceNowRecords.size());
        assertNotNull(serviceNowRecords.get(0).get("number"));
    }

    @Test
    public void readRecordByNumber() throws IOException {
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        configuration.setCommonConfig(apiConfig);

        configuration.setQueryBuilder(new ArrayList<QueryBuilder>() {{
            add(new QueryBuilder(QueryBuilder.Fields.number, QueryBuilder.Operation.Equals, "INC0023046"));
        }});
        configuration.setMaxRecords(READ_ALL_RECORD_FROM_SERVER);
        configuration.getCommonConfig().getFields().add(QueryBuilder.Fields.number.name());

        // We create the component mapper instance using the configuration filled above
        final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                configurationByExample(configuration, "tableDataSet"))
                .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));
        final List<ObjectMap> serviceNowRecords = COMPONENT_FACTORY.collect(ObjectMap.class, mapper, 2, 1)
                .collect(toList());

        assertEquals(1, serviceNowRecords.size());
        assertNotNull(serviceNowRecords.get(0).get("number"));
        assertEquals("INC0023046", serviceNowRecords.get(0).get("number"));
    }

    @Test
    public void produceWithReadFullDataSet() throws IOException {
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        configuration.setCommonConfig(apiConfig);
        configuration.setMaxRecords(READ_ALL_RECORD_FROM_SERVER);

        configuration.setQueryBuilder(new ArrayList<QueryBuilder>() {{
            add(new QueryBuilder(QueryBuilder.Fields.active, QueryBuilder.Operation.Equals, "true"));
            add(new QueryBuilder(QueryBuilder.Fields.priority, QueryBuilder.Operation.Equals, "3"));
        }});
        configuration.setOrdered(true);
        configuration.setOrder(new ArrayList<OrderBuilder>() {{
            add(new OrderBuilder(QueryBuilder.Fields.number, OrderBuilder.Order.ASC));
            add(new OrderBuilder(QueryBuilder.Fields.category, OrderBuilder.Order.DESC));
        }});

        configuration.getCommonConfig().getFields().add(QueryBuilder.Fields.number.name());
        configuration.getCommonConfig().getFields().add(QueryBuilder.Fields.active.name());
        configuration.getCommonConfig().getFields().add(QueryBuilder.Fields.short_description.name());

        final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                configurationByExample(configuration, "tableDataSet"))
                .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));

        final List<ObjectMap> serviceNowRecords = COMPONENT_FACTORY.collect(ObjectMap.class, mapper, 1000, 2)
                .collect(toList());
        assertEquals(1000, serviceNowRecords.size());
        assertNotNull(serviceNowRecords.get(0).get("number"));
    }
}