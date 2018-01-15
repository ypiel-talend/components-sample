package org.talend.components.servicenow.configuration;

import java.io.Serializable;

import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "commonConfig" }),
        @GridLayout.Row({ "actionOnTable" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "commonConfig" }),
        @GridLayout.Row({ "noResponseBody" })
})
public class OutputConfig implements Serializable {

    @Option
    private BasicAuthConfig dataStore;

    @Option
    private CommonConfig commonConfig;

    @Option
    @Documentation("Action to execute on a table, Insert, Update, Delete")
    private ActionOnTable actionOnTable = ActionOnTable.Insert;

    @Option
    @Documentation("Activate or deactivate the response body after the action. Default is true")
    private boolean noResponseBody = true;

    public enum ActionOnTable {
        Insert,
        Update,
        Delete
    }

}