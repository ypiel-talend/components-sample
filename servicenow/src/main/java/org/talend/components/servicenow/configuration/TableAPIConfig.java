package org.talend.components.servicenow.configuration;

import lombok.Data;

import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@GridLayout({
        @GridLayout.Row({ "tableName" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "fields" }),
        @GridLayout.Row({ "excludeReferenceLink" })
})
@Documentation("Table Configuration")
public class TableAPIConfig {

    public static final String Proposable_GetTableFields = "GetTableFields";

    @Option
    @Documentation("The name of the table to be read")
    private String tableName;

    @Option
    @Documentation("List of field names to return in the response.")
    @Proposable(Proposable_GetTableFields)
    private List<String> fields;

    @Option
    @Documentation("Additional information provided for reference fields, such as the URI to the reference resource, is suppressed.")
    private boolean excludeReferenceLink = true;

}
