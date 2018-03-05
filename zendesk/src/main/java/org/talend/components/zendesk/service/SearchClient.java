package org.talend.components.zendesk.service;

import javax.json.JsonObject;

import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.http.Header;
import org.talend.sdk.component.api.service.http.HttpClient;
import org.talend.sdk.component.api.service.http.Query;
import org.talend.sdk.component.api.service.http.Request;
import org.talend.sdk.component.api.service.http.Response;

public interface SearchClient extends HttpClient {

    String API_BASE = "api";
    String API_VERSION = "v2";

    String HEADER_Authorization = "Authorization";
    String HEADER_Content_Type = "Content-Type";

    @Request(path = "search.json", method = "GET")
    @Documentation("Perform a search query")
    Response<JsonObject> search(@Header(HEADER_Authorization) String auth,
            @Header(HEADER_Content_Type) String contentType,
            @Query("query") String query,
            @Query("sort_by") String sortBy,
            @Query("sort_order") String sortOrder,
            @Query("page") Integer page
    );
}
