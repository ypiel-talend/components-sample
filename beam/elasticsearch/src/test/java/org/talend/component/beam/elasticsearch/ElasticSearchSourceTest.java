package org.talend.component.beam.elasticsearch;

import static org.junit.Assert.assertNotNull;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.io.Serializable;
import java.util.Map;

import javax.json.JsonObject;

import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.component.beam.elasticsearch.source.ElasticSearchSource;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.manager.ComponentManager;

public class ElasticSearchSourceTest implements Serializable {
	
	@ClassRule
    public static final SimpleComponentRule COMPONENTS = new SimpleComponentRule(ElasticSearchSource.class.getPackage().getName())
            .withIsolatedPackage(ElasticSearchSource.class.getPackage().getName(), ElasticsearchIO.class.getPackage().getName());
	
	@Rule
    public transient final TestPipeline pipeline = TestPipeline.create();
	
//	@Test
    public void run() {
//        final ElasticSearchDataSet config = new ElasticSearchDataSet();
//        config.setHostname("localhost");
//        config.setPort(9300);
//        config.setId("1001");
//        config.setIndex("mkyong");
//        config.setType("posts");
//        final Map<String, String> map = configurationByExample().forInstance(config).configured().toMap();
//        final String plugin = COMPONENTS.getTestPlugins().iterator().next();
//        final PTransform<PBegin, PCollection<JsonObject>> jdbc = PTransform.class
//                .cast(COMPONENTS.asManager().createComponent("ElasticSearch", "Input", ComponentManager.ComponentType.MAPPER, 1, map)
//                        .orElseThrow(() -> new IllegalArgumentException("no elasticsearch input")));
//        pipeline.run().waitUntilFinish();
    }
	
}
