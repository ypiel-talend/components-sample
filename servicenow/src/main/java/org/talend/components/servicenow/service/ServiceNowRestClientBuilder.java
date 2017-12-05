package org.talend.components.servicenow.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.output.OutputConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class ServiceNowRestClientBuilder {

    private BasicAuthConfig dataStore;

    public ServiceNowRestClientBuilder(BasicAuthConfig dataStore) {
        this.dataStore = dataStore;
    }

    public ServiceNowRestClient clientV2() {
        return new ClientV2(this);
    }

    @Slf4j
    public static final class ClientV2 implements ServiceNowRestClient {

        private static final String API_VERSION = "v2";

        private final CloseableHttpClient httpClient;

        private final HttpClientContext context;

        private final ObjectMapper mapper = new ObjectMapper();

        private final HttpHost host;

        private ClientV2(ServiceNowRestClientBuilder config) {
            host = HttpHost.create(config.dataStore.getUrl());
            final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(new AuthScope(host),
                    new UsernamePasswordCredentials(config.dataStore.getUsername(), config.dataStore.getPassword()));

            httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .useSystemProperties()
                    .build();

            AuthCache authCache = new BasicAuthCache();
            authCache.put(host, new BasicScheme());
            context = HttpClientContext.create();
            context.setCredentialsProvider(credentialsProvider);
            context.setAuthCache(authCache);
        }

        private static void validateHttpResponse(final CloseableHttpResponse response) throws HttpException {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 201) {
                String errorDetails = null;
                if (response.getEntity() != null) {
                    try {
                        errorDetails = EntityUtils.toString(response.getEntity());
                    } catch (IOException e) {
                        //no-op, we ignore the details at this step, the code and reason may be sufficient
                    }
                }

                throw new HttpException(
                        "API ERROR: CODE: " + statusCode + ", REASON: " + response.getStatusLine().getReasonPhrase()
                                + "\n" + ofNullable(errorDetails).orElse(""));
            }
        }

        public TableRestClient table() {
            return new TableRestClientV2(this);
        }

        @Override
        public void close() throws IOException {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage());
                }
            }
        }

        public static final class TableRestClientV2 implements TableRestClient {

            private static final String API_TABLE = "table";

            private final ClientV2 client;

            public TableRestClientV2(final ClientV2 clientV2) {
                this.client = clientV2;
            }

            @Override
            public long estimateRecordBytesSize(final String tableName) {
                URI uri;
                try {
                    uri = new URIBuilder().setScheme(client.host.getSchemeName())
                            .setHost(client.host.getHostName())
                            .setPath(API_BASE + "/" + API_VERSION + "/" + API_TABLE + "/" + tableName)
                            .setParameter(sysparm_exclude_reference_link, "true")
                            .setParameter(sysparm_limit, String.valueOf(1))
                            .build();

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader("Accept", "application/json");
                try (CloseableHttpResponse response = client.httpClient.execute(httpGet, client.context)) {
                    validateHttpResponse(response);
                    return EntityUtils.toByteArray(response.getEntity()).length;
                } catch (IOException | HttpException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public long estimateDataSetBytesSize(final TableDataSet dataSet) {
                URI uri;
                try {
                    uri = new URIBuilder().setScheme(client.host.getSchemeName())
                            .setHost(client.host.getHostName())
                            .setPath(API_BASE + "/" + API_VERSION + "/" + API_TABLE + "/" + dataSet.getTableAPIConfig()
                                    .getTableName())
                            .setParameter(sysparm_exclude_reference_link, "true")
                            .setParameter(sysparm_limit, String.valueOf(1))
                            .build();

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader("Accept", "application/json");
                try (CloseableHttpResponse response = client.httpClient.execute(httpGet, client.context)) {
                    validateHttpResponse(response);
                    int dataSetCount = 0;
                    if (response.getFirstHeader(HEADER_X_Total_Count) != null) {
                        dataSetCount = Integer.valueOf(response.getFirstHeader(HEADER_X_Total_Count).getValue());
                    }
                    int recordSize = 0;
                    if (response.getEntity() != null) {
                        recordSize = EntityUtils.toByteArray(response.getEntity()).length;
                    }
                    return dataSet.getMaxRecords() == 0 ?
                            recordSize * dataSetCount :
                            recordSize * Math.min(dataSetCount, dataSet.getMaxRecords());
                } catch (IOException | HttpException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void healthCheck() throws Exception {
                URI uri;
                try {
                    uri = new URIBuilder().setScheme(client.host.getSchemeName())
                            .setHost(client.host.getHostName())
                            .setPath(API_BASE + "/" + API_VERSION + "/" + API_TABLE + "/incident")
                            .setParameter(sysparm_exclude_reference_link, "true")
                            .setParameter(sysparm_limit, String.valueOf(1))
                            .build();

                    HttpGet httpGet = new HttpGet(uri);
                    httpGet.setHeader("Accept", "application/json");
                    try (CloseableHttpResponse response = client.httpClient.execute(httpGet, client.context)) {
                        validateHttpResponse(response);
                    }
                } catch (URISyntaxException | IOException | HttpException e) {
                    throw new Exception(e);
                }
            }

            @Override
            public void createRecord(final OutputConfig outputConfig, final TableRecord record) {
                URI uri;
                try {
                    uri = new URIBuilder().setScheme(client.host.getSchemeName())
                            .setHost(client.host.getHostName())
                            .setPath(API_BASE + "/" + API_VERSION + "/" + API_TABLE + "/"
                                    + outputConfig.getTableAPIConfig().getTableName())
                            .setParameter(sysparm_exclude_reference_link,
                                    String.valueOf(outputConfig.getTableAPIConfig().isExcludeReferenceLink()))
                            .setParameter(sysparm_limit, String.valueOf(1))
                            .build();

                    HttpPost httpPost = new HttpPost(uri);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader(HEADER_X_no_response_body, String.valueOf(outputConfig.isNoResponseBody()));
                    httpPost.setEntity(new StringEntity(client.mapper.writeValueAsString(record.getData())));
                    try (CloseableHttpResponse response = client.httpClient.execute(httpPost, client.context)) {
                        validateHttpResponse(response);
                        if (!outputConfig.isNoResponseBody()) {
                            JsonNode newRecord =
                                    client.mapper.readValue(EntityUtils.toString(response.getEntity()), JsonNode.class);
                            if (newRecord != null && newRecord.has("result")) {
                                log.info(newRecord.get("result").asText());
                            }
                        }
                    }
                } catch (URISyntaxException | IOException | HttpException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int count(final TableDataSet dataSet) {
                URI uri;
                try {
                    TableDataSet countDataSet = new TableDataSet(dataSet);
                    countDataSet.setOffset(0);
                    countDataSet.setMaxRecords(1);
                    countDataSet.setLimit(1);
                    uri = buildGetUri(countDataSet);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader("Accept", "application/json");
                try (CloseableHttpResponse response = client.httpClient.execute(httpGet, client.context)) {
                    validateHttpResponse(response);
                    return Integer.valueOf(response.getFirstHeader(HEADER_X_Total_Count).getValue());
                } catch (IOException | HttpException e) {
                    throw new RuntimeException(e);
                }
            }

            private URI buildGetUri(final TableDataSet dataSet) throws URISyntaxException {
                final URIBuilder uriBuilder = new URIBuilder().setScheme(client.host.getSchemeName())
                        .setHost(client.host.getHostName())
                        .setPath(API_BASE + "/" + API_VERSION + "/" + API_TABLE + "/" + dataSet.getTableAPIConfig()
                                .getTableName())
                        .setParameter(sysparm_suppress_pagination_header, "true")
                        //.setParameter(glide_invalid_query_returns_no_rows, String.valueOf(dataSet.isNoRowsWithInvalidQuery()))
                        .setParameter(sysparm_offset, String.valueOf(dataSet.getOffset()))
                        .setParameter(sysparm_limit, String.valueOf(
                                dataSet.getOffset() + dataSet.getPageSize() <= dataSet.getMaxRecords() ?
                                        dataSet.getPageSize() :
                                        dataSet.getMaxRecords()));
                if (dataSet.getTableAPIConfig().isExcludeReferenceLink()) {
                    uriBuilder.setParameter(sysparm_exclude_reference_link, "true");
                }

                if (dataSet.getQuery() != null && !dataSet.getQuery().isEmpty()) {
                    uriBuilder.setParameter(sysparm_query, dataSet.getQuery());
                }

                if (dataSet.getTableAPIConfig().getFields() != null && !dataSet.getTableAPIConfig()
                        .getFields()
                        .isEmpty()) {
                    uriBuilder.setParameter(sysparm_fields, dataSet.getFieldsCommaSeparated());
                }

                return uriBuilder.build();
            }


            @Override
            public List<TableRecord> get(final TableDataSet dataSet) {
                URI uri;
                try {
                    uri = buildGetUri(dataSet);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

                HttpGet httpGet = new HttpGet(uri);
                httpGet.setHeader("Accept", "application/json");
                try (CloseableHttpResponse response = client.httpClient.execute(httpGet, client.context)) {
                    validateHttpResponse(response);

                    Map<String, Object> record =
                            client.mapper.readValue(EntityUtils.toString(response.getEntity()), HashMap.class);

                    if (record != null && record.containsKey("result")) {
                        return ((List<Map<String, Object>>) record.get("result")).stream()
                                .map(result -> new TableRecord(result))
                                .collect(toList());
                    }
                } catch (IOException | HttpException e) {
                    throw new RuntimeException(e);
                }

                return emptyList();
            }
        }
    }
}