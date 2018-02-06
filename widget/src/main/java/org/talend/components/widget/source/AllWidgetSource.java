package org.talend.components.widget.source;

import static java.util.Collections.singletonList;

import java.io.Serializable;
import java.util.List;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.widget.Code;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.meta.Documentation;

@Version
@Icon(value = Icon.IconType.SMILEY_SATISFIED)
@PartitionMapper(name = "All Widgets")
@Documentation("A source to test some widgets")
public class AllWidgetSource implements Serializable {

    private final AllWidgetConfig config;

    private final JsonBuilderFactory factory;

    public AllWidgetSource(@Option("configuration") final AllWidgetConfig configuration, JsonBuilderFactory factory) {
        this.config = configuration;
        this.factory = factory;
    }

    @Assessor
    public long assess() {
        return 1L;
    }

    @Split
    public List<AllWidgetSource> split() {
        return singletonList(new AllWidgetSource(config, factory));
    }

    @Emitter
    public AllWidgetWorker createWorker() {
        return new AllWidgetWorker(config, factory);
    }

    @Documentation("widget for demo")
    public static class AllWidgetConfig {

        @TextArea
        @Option
        @Documentation("")
        private String comment;

        @Code("java")
        @Option
        @Documentation("")
        private String javaCode;

        @Code("sql")
        @Option
        @Documentation("")
        private String sqlCode;

    }

    public static class AllWidgetWorker {

        private final AllWidgetConfig config;

        private final JsonBuilderFactory factory;

        public AllWidgetWorker(final AllWidgetConfig config, final JsonBuilderFactory factory) {
            this.config = config;
            this.factory = factory;
        }

        @Producer
        public JsonObject next() {
            return this.factory.createObjectBuilder().add("data", 1).build();
        }
    }

}
