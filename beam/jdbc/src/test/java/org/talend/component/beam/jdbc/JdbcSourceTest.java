package org.talend.component.beam.jdbc;

import static org.junit.Assert.assertNotNull;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

import java.io.Serializable;
import java.util.Map;

import javax.json.JsonObject;

import org.apache.beam.sdk.io.jdbc.JdbcIO;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.beam.coder.JsonpJsonObjectCoder;
import org.talend.sdk.component.runtime.manager.ComponentManager;

public class JdbcSourceTest implements Serializable {

    @ClassRule
    public static final SimpleComponentRule COMPONENTS = new SimpleComponentRule(JdbcSource.class.getPackage().getName())
            .withIsolatedPackage(JdbcSource.class.getPackage().getName(), JdbcIO.class.getPackage().getName());

    @Rule
    public transient final TestPipeline pipeline = TestPipeline.create();

    @Test
    public void run() {
        final JdbcSource.Config config = new JdbcSource.Config();
        config.setDriver("org.hsqldb.jdbcDriver");
        config.setUrl("jdbc:hsqldb:mem:foo");
        config.setUsername("sa");
        config.setPassword("");
        config.setQuery("SELECT * FROM   INFORMATION_SCHEMA.TABLES");
        final Map<String, String> map = configurationByExample().forInstance(config).configured().toMap();
        final String plugin = COMPONENTS.getTestPlugins().iterator().next();
        final PTransform<PBegin, PCollection<JsonObject>> jdbc = PTransform.class
                .cast(COMPONENTS.asManager().createComponent("Jdbc", "Input", ComponentManager.ComponentType.MAPPER, 1, map)
                        .orElseThrow(() -> new IllegalArgumentException("no jdbc input")));
        PAssert.that(pipeline.apply(jdbc).setCoder(JsonpJsonObjectCoder.of(plugin)))
                .satisfies((SerializableFunction<Iterable<JsonObject>, Void>) input -> {
                    assertNotNull(input.iterator().next());
                    return null;
                });
        pipeline.run().waitUntilFinish();
    }
}
