package org.talend.components.servicenow.service.http;

import java.util.List;

import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.service.http.codec.JsonToTableRecordDecoder;
import org.talend.components.servicenow.service.http.codec.RecordSizeDecoder;
import org.talend.components.servicenow.service.http.codec.TableRecordToJsonEncoder;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.http.Codec;
import org.talend.sdk.component.api.service.http.Header;
import org.talend.sdk.component.api.service.http.HttpClient;
import org.talend.sdk.component.api.service.http.HttpException;
import org.talend.sdk.component.api.service.http.Path;
import org.talend.sdk.component.api.service.http.Query;
import org.talend.sdk.component.api.service.http.Request;
import org.talend.sdk.component.api.service.http.Response;

import lombok.Data;

public interface TableApiClient extends HttpClient {

    String API_BASE = "api/now";
    String API_VERSION = "v2";

    String sysparm_suppress_pagination_header = "sysparm_suppress_pagination_header";
    String sysparm_offset = "sysparm_offset";
    String sysparm_limit = "sysparm_limit";
    String sysparm_exclude_reference_link = "sysparm_exclude_reference_link";
    String sysparm_query = "sysparm_query";
    String sysparm_fields = "sysparm_fields";

    String HEADER_X_Total_Count = "X-Total-Count";
    String HEADER_X_no_response_body = "X-no-response-body";
    String HEADER_Authorization = "Authorization";

    @Request(path = "table/{tableName}")
    @Codec(decoder = JsonToTableRecordDecoder.class)
    @Documentation("read record from the table according to the data set definition")
    Response<List<TableRecord>> get(@Path("tableName") String tableName,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Query(sysparm_query) String query,
            @Query(sysparm_fields) String fields,
            @Query(sysparm_offset) int offset,
            @Query(sysparm_limit) int limit,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            @Query(sysparm_suppress_pagination_header) boolean suppressPaginationHeader
    );

    @Request(path = "table/{tableName}")
    @Codec(decoder = RecordSizeDecoder.class)
    @Documentation("read record from the table according to the data set definition")
    long estimateRecordSize(@Path("tableName") String tableName,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Query(sysparm_query) String query,
            @Query(sysparm_fields) String fields,
            @Query(sysparm_offset) int offset,
            @Query(sysparm_limit) int limit,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            @Query(sysparm_suppress_pagination_header) boolean suppressPaginationHeader
    );

    @Request(path = "table/{tableName}", method = "POST")
    @Codec(encoder = TableRecordToJsonEncoder.class, decoder = JsonToTableRecordDecoder.class)
    @Documentation("Create a record to table")
    List<TableRecord> create(@Path("tableName") String tableName,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            TableRecord record);

    default List<TableRecord> getRecords(String tableName, String auth, String query, String fields, int offset,
            int limit, boolean excludeReferenceLink) {
        return get(tableName, auth, false, query, fields, offset, limit, excludeReferenceLink, true).body();
    }

    default int count(String tableName, String auth, String query) {
        return Integer.parseInt(
                get(tableName, auth, true, query, null, 0, 1, true, true).headers()
                        .get(HEADER_X_Total_Count)
                        .iterator()
                        .next());
    }

    default long estimateRecordSize(String tableName, String auth, String query, String fields) {
        return estimateRecordSize(tableName, auth, false, query, fields, 0, 1, true, true);
    }

    default void healthCheck(String auth) {
        final Response<List<TableRecord>> resp =
                get(CommonConfig.Tables.incident.name(), auth, true, null, null, 0, 1, true, true);
        if (resp.status() != 200) {
            throw new HttpException(resp);
        }
    }

    @Data class Status {

        private String status;

        private Error error;

        @Override public String toString() {
            return "status: " + this.status + ", message: " + this.error.message + ", detail: " + this.error.detail;
        }
    }

    @Data class Error {

        private String message;

        private String detail;

    }
}
