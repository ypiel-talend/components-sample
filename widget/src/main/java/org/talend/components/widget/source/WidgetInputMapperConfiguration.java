package org.talend.components.widget.source;

import java.io.Serializable;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Max;
import org.talend.sdk.component.api.configuration.constraint.Min;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout.FormType;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;

import lombok.Data;

@GridLayout(value = {
    @GridLayout.Row({ "dataset" }),
    @GridLayout.Row({ "limit" }),
    @GridLayout.Row({ "tableComplex", "action" }),
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
    
    @Option
    @Min(10)
    @Max(1000)
    private int limit = 10;
    
    /**
     * This is dropdown list
     */
    @Option
    private Action action = Action.CREATE;
    
    @Option
    private TableComplexConfiguration tableComplex;
    
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
    @ActiveIf(target = "showQuery", value = "true")
    @TextArea
    private String query = "SELECT * FROM table";
    
    @Option
    @ActiveIf(target = "showTable", value = "true")
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