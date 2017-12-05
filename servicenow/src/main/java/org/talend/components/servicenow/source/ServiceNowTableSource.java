package org.talend.components.servicenow.source;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.servicenow.configuration.ServiceNowTableDataSet;
import org.talend.components.servicenow.configuration.ServiceNowRecord;
import org.talend.components.servicenow.service.ServiceNowRestClient;
import org.talend.components.servicenow.service.ServiceNowRestClientBuilder;
import org.talend.components.servicenow.service.ServiceNowTableService;
import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;

import static org.talend.components.servicenow.configuration.ServiceNowTableDataSet.READ_ALL_RECORD_FROM_SERVER;

public class ServiceNowTableSource implements Serializable {

    private final ServiceNowTableDataSet tableDataSet;

    private ServiceNowRestClient restClient;

    private BufferizedProducerSupport<ServiceNowRecord> bufferedReader;

    public ServiceNowTableSource(@Option("tableDataSet") final ServiceNowTableDataSet tableDataSet) {
        this.tableDataSet = tableDataSet;

    }

    @PostConstruct
    public void init() {
        restClient = new ServiceNowRestClientBuilder(tableDataSet.getDataStore()).clientV2();

        bufferedReader = new BufferizedProducerSupport<>(() -> {
            if (tableDataSet.getMaxRecords() != READ_ALL_RECORD_FROM_SERVER && tableDataSet.getOffset() >= tableDataSet
                    .getMaxRecords()) {
                return null;//stop reading from this source.
            }

            //Read next page from data set
            final List<ServiceNowRecord> result = restClient.table().get(tableDataSet);

            //advance the data set offset
            if (tableDataSet.getOffset() < tableDataSet.getMaxRecords()) {
                tableDataSet.setOffset(tableDataSet.getOffset() + tableDataSet.getPageSize());
            }

            return result.iterator();
        });
    }

    @Producer
    public ServiceNowRecord next() {
        return bufferedReader.next();
    }

    @PreDestroy
    public void release() {
        try {
            restClient.close();
        } catch (IOException e) {
            //no-op
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}