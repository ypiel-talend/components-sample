package org.talend.components.datastore;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = Icon.IconType.BADGE)
@Emitter(name = "Actors")
@Documentation("")
public class DataStoreComponent implements Serializable {

    private final JsonBuilderFactory jsonBuilderFactory;

    private final Random rand = new Random();

    private final List<String> data = asList("Spider-Man",
            "Iron man",
            "Hulk",
            "Thanos",
            "Thor",
            "Woverine"
    );

    public DataStoreComponent(@Option CDataSet configuration, final JsonBuilderFactory jsonBuilderFactory) {
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @Producer
    public JsonObject next() {
        return jsonBuilderFactory.createObjectBuilder()
                .add("name", data.get(rand.nextInt(data.size()))).build();
    }

}
