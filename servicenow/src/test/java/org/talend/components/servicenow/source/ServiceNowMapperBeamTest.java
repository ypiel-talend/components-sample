package org.talend.components.servicenow.source;

import static java.util.stream.Collectors.joining;
import static org.talend.components.servicenow.ServiceNow.API_URL;
import static org.talend.components.servicenow.ServiceNow.PASSWORD;
import static org.talend.components.servicenow.ServiceNow.USER;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.io.Serializable;
import java.util.Map;

import org.apache.beam.sdk.testing.TestPipeline;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;
import org.talend.sdk.component.runtime.manager.chain.Job;

public class ServiceNowMapperBeamTest implements Serializable {

    @ClassRule
    public transient static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule(
            "org.talend.components.servicenow");

    @ClassRule
    public transient static final JUnit4HttpApi API = new JUnit4HttpApi().activeSsl();

    //    static {
    //        System.setProperty("talend.junit.http.capture", "true");
    //    }

    @Rule
    public transient final TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient final JUnit4HttpApiPerMethodConfigurator configurator =
            new JUnit4HttpApiPerMethodConfigurator(API);

    @Test
    @Ignore("collector don't work in distributed env")
    public void getRecords() {
        final BasicAuthConfig dataStore = new BasicAuthConfig(API_URL, USER, PASSWORD);

        final TableDataSet configuration = new TableDataSet();
        configuration.setDataStore(dataStore);
        final CommonConfig apiConfig = new CommonConfig();
        apiConfig.setTableName(CommonConfig.Tables.incident);
        configuration.setCommonConfig(apiConfig);
        configuration.setMaxRecords(10);

        final Map<String, String> config = configurationByExample(configuration, "tableDataSet");
        final String uriParam = config.keySet().stream().map(k -> k + "=" + config.get(k)).collect(joining("&"));

        Job.components()
                .component("input", "ServiceNow://ServiceNowInput?" + uriParam)
                .component("output", "test://collector")
                .connections()
                .from("input").to("output")
                .build()
                .run();
    }

}
