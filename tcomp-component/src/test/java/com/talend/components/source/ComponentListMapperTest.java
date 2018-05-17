package com.talend.components.source;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.json.JsonObject;

import com.talend.components.source.list.ComponentListMapper;
import com.talend.components.source.list.ComponentListMapperConfiguration;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.runtime.input.Mapper;

public class ComponentListMapperTest {

    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("com.talend.components");

    @Test
    @Ignore("You need to complete this test")
    public void produce() throws IOException {

        // Source configuration
        // Setup your component configuration for the test here
        final TCompStore store = new TCompStore().setUrl("http://127.0.0.1:9090");
        final ComponentListMapperConfiguration configuration =
                new ComponentListMapperConfiguration() //
                        .setFilter_name("nam") //
                        .setFilter_family("fam") //
                        .setDataStore(store);



        // We create the component mapper instance using the configuration filled above
        final Mapper mapper = COMPONENT_FACTORY.createMapper(ComponentListMapper.class, configuration);

        // Collect the source as a list
        assertEquals(asList(/* TODO - give the expected data */),
                COMPONENT_FACTORY.collectAsList(JsonObject.class, mapper));
    }

}