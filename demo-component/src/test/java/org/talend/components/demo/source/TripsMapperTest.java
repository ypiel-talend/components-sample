package org.talend.components.demo.source;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.json.JsonObject;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.input.Mapper;

public class TripsMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("org.talend.components.demo");

    @Test
    @Ignore("You need to complete this test")
    public void produce() throws IOException {

        // Source configuration
        // Setup your component configuration for the test here
        final TripsMapperConfiguration configuration = new TripsMapperConfiguration()
                .setAuth(new AuthConfiguration().setUrl("https://tcl-trip-api.herokuapp.com/api"))
                .setVendor("1");

        // We create the component mapper instance using the configuration filled above
        final Mapper mapper = COMPONENT_FACTORY.createMapper(TripsMapper.class, configuration);

        // Collect the source as a list
        final List<JsonObject> result = COMPONENT_FACTORY.collectAsList(JsonObject.class, mapper);
        assertEquals(10, result.size());
        System.out.println(result);
    }

}