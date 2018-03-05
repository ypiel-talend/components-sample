package org.talend.components.zendesk.source;

import static java.util.Locale.ROOT;
import static org.talend.components.zendesk.service.SearchClient.HEADER_Content_Type;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.components.zendesk.configuration.SearchQuery;
import org.talend.components.zendesk.service.SearchClient;
import org.talend.sdk.component.api.base.BufferizedProducerSupport;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.service.http.Response;

public class SearchSource implements Serializable {

    private final SearchQuery config;

    private final SearchClient searchClient;

    private BufferizedProducerSupport<JsonValue> bufferedReader;

    private transient int page = 0;

    private transient int previousPage = -1;

    public SearchSource(final SearchQuery configuration, final SearchClient searchClient) {
        this.config = configuration;
        this.searchClient = searchClient;
    }

    @PostConstruct
    public void init() {
        bufferedReader = new BufferizedProducerSupport<>(() -> {
            JsonObject result = null;
            if (previousPage == -1) {
                result = search(config.getDataStore().getAuthorizationHeader(),
                        config.getQuery(), config.getSortBy(),
                        config.getSortBy() == null ? null : config.getSortOrder(), null);
            } else if (previousPage != page) {
                result = search(config.getDataStore().getAuthorizationHeader(),
                        config.getQuery(), config.getSortBy(),
                        config.getSortBy() == null ? null : config.getSortOrder(), page);
            }
            if (result == null) {
                return null;
            }
            previousPage = page;
            String nextPage = result.getString("next_page", null);
            if (nextPage != null) {
                page++;
            }

            return result.getJsonArray("results").iterator();
        });
    }

    @Producer
    public JsonObject next() {
        final JsonValue next = bufferedReader.next();
        return next == null ? null : next.asJsonObject();
    }

    private JsonObject search(String auth, String query, String sortBy, String sortOrder, Integer page) {
        final Response<JsonObject> response = searchClient.search(auth, "application/json",
                query, sortBy, sortOrder, page);
        if (response.status() == 200 && response.body().getInt("count") != 0) {
            return response.body();
        }

        final String mediaType = extractMediaType(response.headers());
        if (mediaType != null && mediaType.contains("application/json")) {
            final JsonObject error = response.error(JsonObject.class);
            throw new RuntimeException(error.getString("error") + "\n" + error.getString("description"));
        }
        throw new RuntimeException(response.error(String.class));
    }

    private String extractMediaType(final Map<String, List<String>> headers) {
        final String contentType = headers == null || headers.isEmpty()
                || !headers.containsKey(HEADER_Content_Type) ? null :
                headers.get(HEADER_Content_Type).iterator().next();

        if (contentType == null || contentType.isEmpty()) {
            return null;
        }
        // content-type contains charset and/or boundary
        return ((contentType.contains(";")) ? contentType.split(";")[0] : contentType).toLowerCase(ROOT);
    }
}