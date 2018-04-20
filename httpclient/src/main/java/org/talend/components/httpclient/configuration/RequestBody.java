package org.talend.components.httpclient.configuration;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({
        @GridLayout.Row({ "type" }),
        @GridLayout.Row({ "rawValue" }),
        @GridLayout.Row({ "params" }),
        @GridLayout.Row({ "binaryPath" }),
})
@Documentation("")
public class RequestBody implements Serializable {

    @Option
    @Required
    @Documentation("")
    private Type type = Type.RAW;

    @Option
    @Required
    @TextArea
    @ActiveIf(target = "type", value = "RAW")
    @Documentation("")
    private String rawValue;

    @Option
    @Required
    @ActiveIf(target = "type", value = { "FORM_DATA", "X_WWW_FORM_URLENCODED" })
    @Documentation("")
    private Set<Param> params = new HashSet<>();

    @Option
    @Required
    @ActiveIf(target = "type", value = "BINARY")
    @Documentation("")
    private String binaryPath;

    public enum Type {
        RAW,
        FORM_DATA,
        X_WWW_FORM_URLENCODED,
        BINARY
    }
}
