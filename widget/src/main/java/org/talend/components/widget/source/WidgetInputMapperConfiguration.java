package org.talend.components.widget.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

@GridLayout({
    @GridLayout.Row({ "Connection" }),
    @GridLayout.Row({ "Table" }),
    @GridLayout.Row({ "Batch" })
})
public class WidgetInputMapperConfiguration implements Serializable {

    @Option
    private ConnectionConfiguration Connection;

    @Option
    private String Table;

    @Option
    private boolean Batch;

    public ConnectionConfiguration getConnection() {
        return Connection;
    }

    public WidgetInputMapperConfiguration setConnection(ConnectionConfiguration Connection) {
        this.Connection = Connection;
        return this;
    }

    public String getTable() {
        return Table;
    }

    public WidgetInputMapperConfiguration setTable(String Table) {
        this.Table = Table;
        return this;
    }

    public boolean getBatch() {
        return Batch;
    }

    public WidgetInputMapperConfiguration setBatch(boolean Batch) {
        this.Batch = Batch;
        return this;
    }
}