package org.talend.components.servicenow.service;

import org.junit.Before;
import org.junit.Test;

public class ServiceNowClientTest {

    private ServiceNowClient snClient;

    @Before
    public void init() {
        snClient = new ServiceNowClient("https://dev44668.service-now.com", "admin", "serviceNow123!");
    }

    @Test
    public void readTable() {
        snClient.clientV2().readTable("incident", 10);
    }

}
