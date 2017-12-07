package org.talend.components.servicenow.source;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.ServiceNowRestClient;
import org.talend.components.servicenow.service.ServiceNowRestClientBuilder;
import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;

import static org.talend.components.servicenow.configuration.TableDataSet.READ_ALL_RECORD_FROM_SERVER;

public class ServiceNowTableSource implements Serializable {

    private final TableDataSet tableDataSet;

    private final Messages i18n;

    private ServiceNowRestClient restClient;

    private BufferizedProducerSupport<TableRecord> bufferedReader;

    public ServiceNowTableSource(@Option("tableDataSet") final TableDataSet tableDataSet,
            final Messages i18n) {
        this.tableDataSet = tableDataSet;
        this.i18n = i18n;
    }

    @PostConstruct
    public void init() {
        restClient = new ServiceNowRestClientBuilder(tableDataSet.getDataStore(), i18n).clientV2();

        bufferedReader = new BufferizedProducerSupport<>(() -> {
            if (tableDataSet.getMaxRecords() != READ_ALL_RECORD_FROM_SERVER && tableDataSet.getOffset() >= tableDataSet
                    .getMaxRecords()) {
                return null;//stop reading from this source.
            }

            //Read next page from data set
            final List<TableRecord> result = restClient.table().get(tableDataSet);

            //advance the data set offset
            if (tableDataSet.getOffset() < tableDataSet.getMaxRecords()) {
                tableDataSet.setOffset(tableDataSet.getOffset() + tableDataSet.getPageSize());
            }

            return result.iterator();
        });
    }

    @Producer
    public TableRecord next() {
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