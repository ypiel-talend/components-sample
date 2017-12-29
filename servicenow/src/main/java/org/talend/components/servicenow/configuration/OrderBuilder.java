package org.talend.components.servicenow.configuration;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@GridLayout({
        @GridLayout.Row({ "field" }),
        @GridLayout.Row({ "order" }),
})
public class OrderBuilder {

    @Option
    @Documentation("filed to be ordered")
    private QueryBuilder.Fields field;

    @Option
    @Documentation("the order")
    private Order order;

    public enum Order {
        DESC,
        ASC;
    }

}
