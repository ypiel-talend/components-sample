package org.talend.components.zendesk.source;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.zendesk.configuration.BasicAuth;
import org.talend.components.zendesk.configuration.SearchQuery;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApi;
import org.talend.sdk.component.junit.http.junit4.JUnit4HttpApiPerMethodConfigurator;
import org.talend.sdk.component.maven.MavenDecrypter;
import org.talend.sdk.component.maven.Server;
import org.talend.sdk.component.runtime.manager.chain.Job;

public class SearchTest {

    //    static {
    //        System.setProperty("talend.junit.http.capture", "true");
    //    }

    @ClassRule
    public static final SimpleComponentRule component = new SimpleComponentRule("");

    @ClassRule
    public static final JUnit4HttpApi API = new JUnit4HttpApi().activeSsl();

    @Rule
    public final JUnit4HttpApiPerMethodConfigurator configurator = new JUnit4HttpApiPerMethodConfigurator(API);

    private final MavenDecrypter mavenDecrypter = new MavenDecrypter();

    @Test
    public void test() {
        //final Server server = mavenDecrypter.find("zendesk");
        final Server server = new Server();
        server.setUsername("user");
        server.setPassword("nopass");
        final Map<String, String> configuration =
                configurationByExample(new SearchQuery(new BasicAuth("https://component.zendesk.com",
                        server.getUsername(), server.getPassword()),
                        "type:ticket status:open", "relevance", "asc"));

        final String uriConfig = configuration.keySet().stream()
                .map(k -> k + "=" + uriEncode(configuration.get(k))).collect(joining("&"));

        Job.components()
                .component("search", "zendesk://search?" + uriConfig)
                .component("collector", "test://collector")
                .connections()
                .from("search").to("collector")
                .build()
                .run();

        final List<JsonObject> res = component.getCollectedData(JsonObject.class);
        assertEquals(4, res.size());
    }

    private String uriEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
