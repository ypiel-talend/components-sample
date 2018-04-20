package org.talend.components.httpclient.service;

import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.talend.components.httpclient.configuration.RequestBody;
import org.talend.components.httpclient.configuration.RequestConfig;
import org.talend.components.httpclient.service.codec.RequestBodyEncoder;
import org.talend.components.httpclient.service.codec.XmlToJsonDecoder;
import org.talend.sdk.component.api.service.http.Codec;
import org.talend.sdk.component.api.service.http.ConfigurerOption;
import org.talend.sdk.component.api.service.http.Headers;
import org.talend.sdk.component.api.service.http.HttpClient;
import org.talend.sdk.component.api.service.http.HttpMethod;
import org.talend.sdk.component.api.service.http.Query;
import org.talend.sdk.component.api.service.http.QueryParams;
import org.talend.sdk.component.api.service.http.Request;
import org.talend.sdk.component.api.service.http.Response;
import org.talend.sdk.component.api.service.http.Url;
import org.talend.sdk.component.api.service.http.UseConfigurer;

public interface Client extends HttpClient {

    @Request
    @Codec(decoder = { XmlToJsonDecoder.class }, encoder = { RequestBodyEncoder.class })
    @UseConfigurer(RequestConfigurer.class)
    Response<JsonValue> execute(
            @ConfigurerOption("configuration") RequestConfig config,
            @ConfigurerOption("httpClient") Client httpClient,
            @HttpMethod String httpMethod,
            @Url String url,
            @Headers Map<String, String> headers,
            @QueryParams Map<String, String> queryParams,
            RequestBody body);

    @Request(method = "POST")
    Response<JsonObject> token(@Url String url,
            @Query("grant_type") String grantType,
            @Query("username") String username,
            @Query("password") String password,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("scope") String scope);

}
