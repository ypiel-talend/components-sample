package org.talend.components.demo.service;

import javax.json.JsonObject;

import org.talend.sdk.component.api.service.http.HttpClient;
import org.talend.sdk.component.api.service.http.Query;
import org.talend.sdk.component.api.service.http.Request;

public interface RestClient extends HttpClient {

    @Request(path = "trip")
    JsonObject query(@Query("trip_distance_min") Double dMin,
            @Query("trip_distance_max") Double dMax,
            @Query("max_records") Integer maxRecords);

}
