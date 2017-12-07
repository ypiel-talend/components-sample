package org.talend.components.widget.source;

import java.io.Serializable;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout.FormType;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;

import lombok.Data;

@GridLayout(value = {
    @GridLayout.Row({ "dataset" }),
    @GridLayout.Row({ "action" }),
    @GridLayout.Row({ "showQuery", "showTable" }),
    @GridLayout.Row({ "query" }),
    @GridLayout.Row({ "table" })},
    names = FormType.MAIN
)
@GridLayout(value = {
    @GridLayout.Row({ "showCode" }),
    @GridLayout.Row({ "code" })},
    names = FormType.ADVANCED
)
@Data
public class WidgetInputMapperConfiguration implements Serializable {

    @Option
    private DatasetConfiguration dataset;
    
    /**
     * This is dropdown list
     */
    @Option
    private Action action = Action.CREATE;
    
    /**
     * This is checkbox
     */
    @Option
    private boolean showQuery = false;
    
    @Option
    private boolean showTable = false;
    
    @Option
    private boolean showCode = true;
    
    @Option
    @TextArea
    private String query = "SELECT * FROM table";
    
    @Option
    private List<Filter> table;
    
    @Option
    @Code("Java")
    private String code = "String foo = \"bar\";";
    
    public static enum Action {
        CREATE,
        UPDATE,
        DELETE
    }

}