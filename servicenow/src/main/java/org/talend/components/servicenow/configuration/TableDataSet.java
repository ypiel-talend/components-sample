package org.talend.components.servicenow.configuration;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DataSet("table")
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "tableAPIConfig" }),
        @GridLayout.Row({ "query" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "limit" }),
        @GridLayout.Row({ "maxRecords" }),
        @GridLayout.Row({ "fields" })
})
@Documentation("This data set represent a Service Now Table, like incident, problem, service...")
public class TableDataSet implements Serializable {

    public static final int READ_ALL_RECORD_FROM_SERVER = -1;

    public static final int MAX_LIMIT = 10000;

    @Option
    private BasicAuthConfig dataStore;

    @Option
    private TableAPIConfig tableAPIConfig;

    @Option
    @Documentation("Encoded query used to filter the result set. For more details see `sysparm_query` https://developer.servicenow.com/app.do#!/rest_api_doc?v=jakarta&id=r_TableAPI-GET ")
    private String query;

    /**
     * data source start
     */
    private int offset;

    @Option
    @Documentation("Max record to retrieve. Default if -1, set to -1 to get all the data from service now server.")
    private int maxRecords = READ_ALL_RECORD_FROM_SERVER;

    @Option
    @Documentation("limit for pagination. The default is 10000.")
    private int limit = MAX_LIMIT;

    public TableDataSet(TableDataSet mDataSet) {
        this.dataStore = mDataSet.getDataStore();
        this.tableAPIConfig = mDataSet.getTableAPIConfig();
        this.query = mDataSet.getQuery();

        this.offset = mDataSet.getOffset();
        this.maxRecords = mDataSet.getMaxRecords();
        this.limit = mDataSet.getLimit();
    }

    /**
     * @return the total record that can be read from this data set
     */
    public int getPageSize() {
        return maxRecords == READ_ALL_RECORD_FROM_SERVER ? limit : Math.min(limit, maxRecords - offset);
    }

    public String getFieldsCommaSeparated() {
        return ofNullable(tableAPIConfig.getFields()).orElse(emptyList()).stream().collect(joining(","));
    }

}