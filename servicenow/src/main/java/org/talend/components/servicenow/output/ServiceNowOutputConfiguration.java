package org.talend.components.servicenow.output;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "action" }),
    @GridLayout.Row({ "table" })
})
public class ServiceNowOutputConfiguration implements Serializable {
    @Option
    private String action;

    @Option
    private String table;

    public String getAction() {
        return action;
    }

    public ServiceNowOutputConfiguration setAction(String action) {
        this.action = action;
        return this;
    }

    public String getTable() {
        return table;
    }

    public ServiceNowOutputConfiguration setTable(String table) {
        this.table = table;
        return this;
    }
}