package org.talend.components.httpclient.configuration;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({
        @GridLayout.Row({ "key", "value" }),
})
@Documentation("")
public class Param  implements Serializable {

    @Option
    @Required
    @Documentation("")
    private String key;

    @Option
    @Documentation("")
    private String value;

}
