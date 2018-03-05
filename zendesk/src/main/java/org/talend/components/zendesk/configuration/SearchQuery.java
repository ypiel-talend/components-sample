package org.talend.components.zendesk.configuration;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet
@GridLayout({
        @GridLayout.Row({ "dataStore" }),
        @GridLayout.Row({ "query" }),
        @GridLayout.Row({ "sortBy", "sortOrder" })
})
@Documentation("Data set that define a search query for Zendesk Search API. See api reference https://developer.zendesk.com/rest_api/docs/core/search")
public class SearchQuery {

    @Option
    @Documentation("Authentication information.")
    private final BasicAuth dataStore;

    @Option
    @TextArea
    @Documentation("Zendesk Search query.")
    private final String query;

    @Option
    @DefaultValue("relevance")
    @Documentation("One of updated_at, created_at, priority, status, or ticket_type. Defaults to sorting by relevance")
    private final String sortBy;

    @Option
    @DefaultValue("desc")
    @Documentation("One of asc or desc. Defaults to desc")
    private final String sortOrder;
}
