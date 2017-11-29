package org.talend.components.servicenow.configuration;

import lombok.Data;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.OptionsOrder;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

@Data
@org.talend.sdk.component.api.configuration.type.DataSet
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "tableName" })
})
@OptionsOrder({ "dataStore", "tableName" })
public class ServiceNowDataSet implements Serializable {

    @Option
    private DataStore dataStore;

    @Option
    private String tableName;

}