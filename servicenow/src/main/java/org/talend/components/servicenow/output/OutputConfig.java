package org.talend.components.servicenow.output;

import lombok.Data;

import java.io.Serializable;

import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.TableAPIConfig;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

@Data
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "tableAPIConfig" }),
        @GridLayout.Row({ "actionOnTable" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "noResponseBody" })
})
public class OutputConfig implements Serializable {

    @Option
    private BasicAuthConfig dataStore;

    @Option
    private TableAPIConfig tableAPIConfig;

    @Option
    private ActionOnTable actionOnTable = ActionOnTable.Insert;

    @Option
    private boolean noResponseBody = true;

    public enum ActionOnTable {
        Insert,
        Update,
        Delete
    }

}