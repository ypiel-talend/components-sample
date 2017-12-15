package org.talend.components.servicenow.configuration;

import java.io.Serializable;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({
        @GridLayout.Row({ "tableName" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "fields" }),
        @GridLayout.Row({ "excludeReferenceLink" })
})
@Documentation("Table Configuration")
public class TableAPIConfig implements Serializable {

    public static final String Proposable_GetTableFields = "GetTableFields";

    @Option
    @Documentation("The name of the table to be read")
    private Tables tableName;

    @Option
    @Documentation("List of field names to return in the response.")
    @Proposable(Proposable_GetTableFields)
    private List<String> fields;

    @Option
    @Documentation("Additional information provided for reference fields, such as the URI to the reference resource, is suppressed.")
    private boolean excludeReferenceLink = true;

    public enum Tables {
        incident,
        problem,
        change_request,
        sc_request,
        sc_cat_item,
    }

}
