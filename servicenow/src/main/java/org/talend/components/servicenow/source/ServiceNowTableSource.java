package org.talend.components.servicenow.source;

import static org.talend.components.servicenow.configuration.TableDataSet.READ_ALL_RECORD_FROM_SERVER;
import static org.talend.components.servicenow.service.http.TableApiClient.API_BASE;
import static org.talend.components.servicenow.service.http.TableApiClient.API_VERSION;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.processor.data.ObjectMap;

public class ServiceNowTableSource implements Serializable {

    private final TableDataSet ds;

    private final Messages i18n;

    private BufferizedProducerSupport<ObjectMap> bufferedReader;

    private TableApiClient tableAPI;

    public ServiceNowTableSource(@Option("ds") final TableDataSet ds,
            final Messages i18n, TableApiClient tableAPI) {
        this.ds = ds;
        this.i18n = i18n;
        this.tableAPI = tableAPI;
    }

    @PostConstruct
    public void init() {
        tableAPI.base(ds.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
        bufferedReader = new BufferizedProducerSupport<>(() -> {
            if (ds.getMaxRecords() != READ_ALL_RECORD_FROM_SERVER && ds.getOffset() >= ds
                    .getMaxRecords()) {
                return null;//stop reading from this source.
            }

            //Read next page from data set
            final List<ObjectMap> result = tableAPI.getRecords(ds.getCommonConfig().getTableName().name(),
                    ds.getDataStore().getAuthorizationHeader(),
                    ds.buildQuery(),
                    ds.getCommonConfig().getFieldsCommaSeparated(),
                    ds.getOffset(),
                    evalLimit(ds),
                    ds.getCommonConfig().isExcludeReferenceLink()
            );

            //advance the data set offset
            if (ds.getOffset() < ds.getMaxRecords()) {
                ds.setOffset(ds.getOffset() + ds.getPageSize());
            }

            return result.iterator();
        });
    }

    private int evalLimit(final TableDataSet ds) {
        return ds.getOffset() + ds.getPageSize() <= ds.getMaxRecords() ? ds.getPageSize() : ds.getMaxRecords();
    }

    @Producer
    public ObjectMap next() {
        return bufferedReader.next();
    }
}