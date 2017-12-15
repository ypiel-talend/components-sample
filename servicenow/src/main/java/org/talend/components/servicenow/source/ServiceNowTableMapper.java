package org.talend.components.servicenow.source;

import static java.util.stream.Collectors.toList;
import static org.talend.components.servicenow.configuration.TableDataSet.READ_ALL_RECORD_FROM_SERVER;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.LongStream;

import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.ServiceNowRestClient;
import org.talend.components.servicenow.service.ServiceNowRestClientBuilder;
import org.talend.components.servicenow.service.ServiceNowTableService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "ServiceNowInput")
@PartitionMapper(name = "ServiceNowInput")
@Documentation("ServiceNowInput is a configurable connector that is responsible of reading from Service Now Table using a query to filter the records.")
public class ServiceNowTableMapper implements Serializable {

    private final TableDataSet tableDataSet;

    private final ServiceNowTableService service;

    private final Messages i18n;

    public ServiceNowTableMapper(@Option("tableDataSet") final TableDataSet tableDataSet,
            final ServiceNowTableService service, final Messages i18n) {
        this.tableDataSet = tableDataSet;
        this.service = service;
        this.i18n = i18n;
    }

    @Assessor
    public long estimateSize() {
        try (ServiceNowRestClient sNRestClient = new ServiceNowRestClientBuilder(tableDataSet.getDataStore(),
                i18n).clientV2()) {
            return sNRestClient.table().estimateDataSetBytesSize(tableDataSet);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Split
    public List<ServiceNowTableMapper> split(@PartitionSize final long bundles) {
        try (ServiceNowRestClient sNRestClient = new ServiceNowRestClientBuilder(
                tableDataSet.getDataStore(), i18n).clientV2()) {
            long recordSize =
                    sNRestClient.table()
                            .estimateRecordBytesSize(tableDataSet.getTableAPIConfig().getTableName().name());
            long nbBundle = Math.max(1, estimateSize() / bundles);
            final long bundleCount = bundles / recordSize;
            final int totalData = sNRestClient.table().count(tableDataSet);//total data in the server
            final int requestedSize = tableDataSet.getMaxRecords() == READ_ALL_RECORD_FROM_SERVER ?
                    totalData :
                    Math.min(totalData, tableDataSet.getMaxRecords());

            return LongStream.range(0, nbBundle).mapToObj(i -> {
                final int from = (int) (bundleCount * i);
                final int to = (i == nbBundle - 1) ? requestedSize : (int) (from + bundleCount);
                final TableDataSet dataSetChunk = new TableDataSet(tableDataSet);
                dataSetChunk.setOffset(from);
                dataSetChunk.setMaxRecords(to);
                return new ServiceNowTableMapper(dataSetChunk, service, i18n);
            }).collect(toList());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Emitter
    public ServiceNowTableSource createWorker() {
        return new ServiceNowTableSource(tableDataSet, i18n);
    }
}