package org.talend.components.servicenow.service;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.talend.components.servicenow.configuration.ServiceNowRecord;
import org.talend.sdk.component.api.service.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.util.Collections.emptyList;

@Service
public class ServiceNowClient {

    public static final String API_BASE = "/api/now/";

    private String protocol;

    private String host;

    private CredentialsProvider credentialsProvider;

    public ServiceNowClient(String url, String userName, String password) {
        protocol = url.startsWith("https://") ? "https" : "http";
        this.host = url.replace("http://", "") //remove protocol
                       .replace("https://", "")
                       .replaceAll("(/$)", "");//remove last /
        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider
                .setCredentials(new AuthScope(new HttpHost(host)), new UsernamePasswordCredentials(userName, password));
    }

    public ClientV2 clientV2() {
        return new ClientV2(protocol, host, credentialsProvider);
    }

    @Slf4j
    public static class ClientV2 implements Closeable {

        private static final String API_BASE = ServiceNowClient.API_BASE + "v2/";

        private final String protocol;

        private final String host;

        private final CloseableHttpClient httpClient;

        private final ObjectMapper mapper = new ObjectMapper();

        public ClientV2(String protocol, String host, CredentialsProvider cp) {
            this.protocol = protocol;
            this.host = host;
            httpClient = HttpClients.custom()
                                    .setDefaultCredentialsProvider(cp)
                                    .build();
        }

        public List<ServiceNowRecord> readTable(String tableName, int limit) {
            URI uri;
            try {
                uri = new URIBuilder().setScheme(protocol).setHost(host)
                                      .setPath(API_BASE + "table/" + tableName)
                                      .setParameter("sysparm_exclude_reference_link", "true")
                                      .setParameter("sysparm_limit", String.valueOf(limit)).build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("Accept", "application/json");
            try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("API ERROR: code=" + statusCode
                            + ", message=" + response.getStatusLine().getReasonPhrase());
                }

                JsonNode jsonResponse = mapper.readValue(response.getEntity().getContent(), JsonNode.class).get("result");
                jsonResponse.isArray();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return emptyList();
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
    }

}