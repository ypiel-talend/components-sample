package org.talend.components.servicenow.source;

import io.specto.hoverfly.junit.core.Hoverfly;

import java.io.IOException;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.ServiceNowTableDataSet;
import org.talend.components.servicenow.configuration.ServiceNowBasicAuth;
import org.talend.components.servicenow.configuration.ServiceNowRecord;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.input.Mapper;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static io.specto.hoverfly.junit.core.SimulationSource.defaultPath;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.talend.components.servicenow.configuration.ServiceNowTableDataSet.READ_ALL_RECORD_FROM_SERVER;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

public class ServiceNowMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("org.talend.components.servicenow");

    //    @ClassRule
    //    public static HoverflyRule hoverflyRule = HoverflyRule
    //            .inCaptureMode("C:\\Users\\akhabali\\Documents\\hoverfly\\incidents_WithInvalidQuery.json");

    @Test
    public void produceWithConcurrency() throws IOException {
        try (Hoverfly hoverfly = new Hoverfly(configs(), SIMULATE)) {
            hoverfly.start();
            hoverfly.simulate(defaultPath("incidents_1000.json"));

            ServiceNowBasicAuth dataStore = new ServiceNowBasicAuth();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final ServiceNowTableDataSet configuration = new ServiceNowTableDataSet();
            configuration.setDataStore(dataStore);
            configuration.setTableName("incident");
            configuration.setMaxRecords(1000);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                    configurationByExample(configuration, "tableDataSet")).orElseThrow(() -> new RuntimeException("fail"));
            final List<ServiceNowRecord> serviceNowRecords = COMPONENT_FACTORY.collect(ServiceNowRecord.class, mapper, 2000, 24)
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

            ServiceNowBasicAuth dataStore = new ServiceNowBasicAuth();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final ServiceNowTableDataSet configuration = new ServiceNowTableDataSet();
            configuration.setDataStore(dataStore);
            configuration.setTableName("incident");
            configuration.setQuery("active=true^ORDERBYnumber^ORDERBYDESCcategory");
            configuration.setMaxRecords(100);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                    configurationByExample(configuration, "tableDataSet"))
                                                   .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));

            final List<ServiceNowRecord> serviceNowRecords = COMPONENT_FACTORY.collect(ServiceNowRecord.class, mapper, 1000, 2)
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
            ServiceNowBasicAuth dataStore = new ServiceNowBasicAuth();
            dataStore.setUsername("fakeUserName");
            dataStore.setPassword("fakePassword");
            dataStore.setUrl("https://dev44668.service-now.com");
            final ServiceNowTableDataSet configuration = new ServiceNowTableDataSet();
            configuration.setDataStore(dataStore);
            configuration.setTableName("incident");
            configuration.setQuery("active=true^priority=1^ORDERBYnumber^ORDERBYDESCcategory");
            configuration.setMaxRecords(READ_ALL_RECORD_FROM_SERVER);

            // We create the component mapper instance using the configuration filled above
            final Mapper mapper = COMPONENT_FACTORY.asManager().findMapper("ServiceNow", "ServiceNowInput", 1,
                    configurationByExample(configuration, "tableDataSet"))
                                                   .orElseThrow(() -> new RuntimeException("fail, can't find configuration"));

            final List<ServiceNowRecord> serviceNowRecords = COMPONENT_FACTORY.collect(ServiceNowRecord.class, mapper, 1000, 2)
                                                                              .collect(toList());
            assertEquals(1000, serviceNowRecords.size());
            assertNotNull(serviceNowRecords.get(0).getData().get("number"));
        }
    }

}