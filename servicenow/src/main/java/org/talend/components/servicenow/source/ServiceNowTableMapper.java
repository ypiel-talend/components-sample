package org.talend.components.servicenow.source;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.LongStream;

import org.talend.components.servicenow.configuration.ServiceNowTableDataSet;
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

import static java.util.stream.Collectors.toList;
import static org.talend.components.servicenow.configuration.ServiceNowTableDataSet.READ_ALL_RECORD_FROM_SERVER;

//
// this class role is to enable the work to be distributed in environments supporting it.
// default version is 1, if some tableDataSet changes happen between 2 versions you can add a migrationHandler
@Version
@Icon(Icon.IconType.STAR)
@PartitionMapper(name = "ServiceNowInput")
public class ServiceNowTableMapper implements Serializable {

    private final ServiceNowTableDataSet tableDataSet;

    private final ServiceNowTableService service;

    public ServiceNowTableMapper(@Option("tableDataSet") final ServiceNowTableDataSet tableDataSet,
            ServiceNowTableService service) {
        this.tableDataSet = tableDataSet;
        this.service = service;
    }

    @Assessor
    public long estimateSize() {
        try (ServiceNowRestClient sNRestClient = new ServiceNowRestClientBuilder(
                tableDataSet.getDataStore()).clientV2()) {
            return sNRestClient.table().estimateDataSetBytesSize(tableDataSet);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Split
    public List<ServiceNowTableMapper> split(@PartitionSize final long bundles) {
        try (ServiceNowRestClient sNRestClient = new ServiceNowRestClientBuilder(
                tableDataSet.getDataStore()).clientV2()) {
            long recordSize = sNRestClient.table().estimateRecordBytesSize(tableDataSet.getTableName());
            long nbBundle = Math.max(1, estimateSize() / bundles);
            final long bundleCount = bundles / recordSize;
            final int totalData = sNRestClient.table().count(tableDataSet);//total data in the server
            final int requestedSize = tableDataSet.getMaxRecords() == READ_ALL_RECORD_FROM_SERVER ?
                    totalData :
                    Math.min(totalData, tableDataSet.getMaxRecords());

            return LongStream.range(0, nbBundle).mapToObj(i -> {
                final int from = (int) (bundleCount * i);
                final int to = (i == nbBundle - 1) ? requestedSize : (int) (from + bundleCount);
                final ServiceNowTableDataSet dataSetChunk = new ServiceNowTableDataSet(tableDataSet);
                dataSetChunk.setOffset(from);
                dataSetChunk.setMaxRecords(to);
                return new ServiceNowTableMapper(dataSetChunk, service);
            }).collect(toList());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Emitter
    public ServiceNowTableSource createWorker() {
        // here we create an actual worker,
        // you are free to rework the tableDataSet etc but our default generated implementation
        // propagates the partition mapper entries.
        return new ServiceNowTableSource(tableDataSet);
    }
}