package org.talend.components.servicenow.service.http;

import java.util.List;

import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.service.http.codec.InvalidContentDecoder;
import org.talend.components.servicenow.service.http.codec.JsonDecoder;
import org.talend.components.servicenow.service.http.codec.JsonEncoder;
import org.talend.components.servicenow.service.http.codec.RecordSizeDecoder;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.data.ObjectMap;
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
    String HEADER_Content_Type = "Content-Type";

    @Request(path = "table/{tableName}")
    @Codec(decoder = { JsonDecoder.class, InvalidContentDecoder.class })
    @Documentation("read record from the table according to the data set definition")
    Response<List<ObjectMap>> get(@Path("tableName") String tableName,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Query(sysparm_query) String query,
            @Query(sysparm_fields) String fields,
            @Query(sysparm_offset) int offset,
            @Query(sysparm_limit) int limit,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            @Query(sysparm_suppress_pagination_header) boolean suppressPaginationHeader
    );

    default List<ObjectMap> getRecords(String tableName, String auth, String query, String fields, int offset,
            int limit) {
        final Response<List<ObjectMap>> resp =
                get(tableName, auth, false, query, fields, offset, limit, true, true);
        if (resp.status() != 200) {
            throw new HttpException(resp);
        }
        return resp.body();
    }

    default int count(String tableName, String auth, String query) {
        final Response<List<ObjectMap>> resp = get(tableName, auth, true, query, null, 0, 1, true, true);
        if (resp.status() != 200) {
            throw new HttpException(resp);
        }
        final List<String> totalCount = resp.headers().get(HEADER_X_Total_Count);
        if (totalCount == null) {
            return 0;
        }

        return Integer.parseInt(totalCount.iterator().next());
    }

    default void healthCheck(String auth) {
        getRecords(CommonConfig.Tables.incident.name(), auth, null, null, 0, 1);
    }

    @Request(path = "table/{tableName}")
    @Codec(decoder = { RecordSizeDecoder.class, InvalidContentDecoder.class })
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

    default long estimateRecordSize(String tableName, String auth, String query, String fields) {
        return estimateRecordSize(tableName, auth, false, query, fields, 0, 1, true, true);
    }

    @Request(path = "table/{tableName}", method = "POST")
    @Codec(encoder = JsonEncoder.class, decoder = { JsonDecoder.class, InvalidContentDecoder.class })
    @Documentation("Create a record to table")
    Response<ObjectMap> create(@Path("tableName") String tableName,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Header(HEADER_Content_Type) String contentType,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            ObjectMap record);

    default ObjectMap create(String tableName, String auth, boolean noResponseBody, ObjectMap record) {
        final Response<ObjectMap> resp = create(tableName, auth, noResponseBody, "application/json", true, record);
        if (resp.status() != 201) {
            throw new HttpException(resp);
        }
        return resp.body();
    }

    @Request(path = "table/{tableName}/{sysId}", method = "PUT")
    @Codec(encoder = JsonEncoder.class, decoder = { JsonDecoder.class, InvalidContentDecoder.class })
    @Documentation("update a record in table using it sys_id")
    Response<ObjectMap> update(@Path("tableName") String tableName, @Path("sysId") String sysId,
            @Header(HEADER_Authorization) String auth,
            @Header(HEADER_X_no_response_body) boolean noResponseBody,
            @Header(HEADER_Content_Type) String contentType,
            @Query(sysparm_exclude_reference_link) boolean excludeReferenceLink,
            ObjectMap record);

    default ObjectMap update(String tableName, String sysId, String auth, boolean noResponseBody, ObjectMap record) {
        final Response<ObjectMap> resp = update(tableName, sysId, auth, noResponseBody, "application/json",
                true, record);
        if (resp.status() != 200) {
            throw new HttpException(resp);
        }
        return resp.body();
    }

    @Request(path = "table/{tableName}/{sysId}", method = "DELETE")
    @Codec(decoder = { JsonDecoder.class, InvalidContentDecoder.class })
    @Documentation("delete a record from a table by it sys_id")
    Response<Void> delete(@Path("tableName") String tableName, @Path("sysId") String sysId,
            @Header(HEADER_Authorization) String auth);

    default void deleteRecordById(String tableName, String sysId, String auth) {
        final Response<?> resp = delete(tableName, sysId, auth);
        if (resp.status() != 204) {
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
