package org.talend.component.beam.elasticsearch.source;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;

import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO.ConnectionConfiguration;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.runtime.beam.coder.JsonpJsonObjectCoder;

@Icon(Icon.IconType.DB_INPUT)
@Version(1)
@PartitionMapper(name = "Input")
@Documentation("Elasticsearch input component")
public class ElasticSearchSource extends PTransform<PBegin, PCollection<JsonObject>> {
	
	private final ElasticSearchSourceConfiguration config;
	
	private final Jsonb jsonb;
	
	public ElasticSearchSource(@Option("configuration") final ElasticSearchSourceConfiguration config, final Jsonb jsonb) {
		this.config = config;
		this.jsonb = jsonb;
	}

	@Override
	public PCollection<JsonObject> expand(PBegin input) {
		ElasticsearchIO.ConnectionConfiguration conf = config.getDataset().asConnectionConfiguration();
		ElasticsearchIO.Read read = ElasticsearchIO.read().withConnectionConfiguration(conf);
		if(config.getQuery() != null && !config.getQuery().isEmpty()) {
			read = read.withQuery(config.getQuery());
		}
		return input
        .apply(read)
        .apply(ParDo.of(new DoFn<String, JsonObject>() {

            @ProcessElement
            public void onElement(final ProcessContext context) {
                context.output(jsonb.fromJson(context.element(), JsonObject.class));
            }
        })).setCoder(JsonpJsonObjectCoder.of(null));
	}

}
