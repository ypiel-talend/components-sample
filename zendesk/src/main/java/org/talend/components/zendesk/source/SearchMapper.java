package org.talend.components.zendesk.source;

import static org.talend.components.zendesk.service.SearchClient.API_BASE;
import static org.talend.components.zendesk.service.SearchClient.API_VERSION;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.talend.components.zendesk.configuration.SearchQuery;
import org.talend.components.zendesk.service.SearchClient;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = Icon.IconType.CUSTOM, custom = "zendesk")
@PartitionMapper(name = "search")
@Documentation("Search component for zendesk query")
public class SearchMapper implements Serializable {

    private final SearchQuery configuration;

    private final SearchClient searchClient;

    public SearchMapper(@Option("configuration") final SearchQuery configuration, final SearchClient searchClient) {
        this.configuration = configuration;
        this.searchClient = searchClient;
    }

    @PostConstruct
    public void init() {
        searchClient.base(configuration.getDataStore().getUrlWithSlashEnding() + API_BASE + "/" + API_VERSION);
    }

    @Assessor
    public long estimateSize() {
        return 1L;
    }

    @Split
    public List<SearchMapper> split(@PartitionSize final long bundles) {
        return Collections.singletonList(this);
    }

    @Emitter
    public SearchSource createWorker() {
        return new SearchSource(configuration, searchClient);
    }
}