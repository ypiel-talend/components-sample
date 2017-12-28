package org.talend.components.servicenow.output;

import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static java.util.Collections.singletonList;

import io.specto.hoverfly.junit.core.HoverflyConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.ApiSimulationRule;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.sdk.component.api.processor.data.FlatObjectMap;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.output.Processor;

public class ServiceNowOutputTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY =
            new SimpleComponentRule("org.talend.components.servicenow");

    @Rule
    public ApiSimulationRule apiSimulationRule = new ApiSimulationRule(SIMULATE, HoverflyConfig.configs());

    @Test
    public void insertRecord() throws IOException {
        final OutputConfig configuration = new OutputConfig();
        configuration.setDataStore(new BasicAuthConfig("https://dev44668.service-now.com/", "user", "password"));
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

}