package org.talend.component.beam.elasticsearch.output;

import javax.json.JsonObject;
import javax.json.bind.Jsonb;

import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.talend.component.beam.elasticsearch.ElasticSearchDataSet;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.Processor;

@Icon(Icon.IconType.DB_INPUT)
@Version(1)
@Processor(name="Output")
@Documentation("Elasticsearch output component")
public class ElasticSearchOutput extends PTransform<PCollection<JsonObject>, PDone> {

	private final ElasticSearchDataSet dataset;
	
	private final Jsonb jsonb;
	
	public ElasticSearchOutput(@Option("configuration") final ElasticSearchDataSet config, final Jsonb jsonb) {
		this.dataset = config;
		this.jsonb = jsonb;
	}
	
	@Override
	public PDone expand(PCollection<JsonObject> input) {
		ElasticsearchIO.ConnectionConfiguration conf = dataset.asConnectionConfiguration();
		return input.apply(ParDo.of(new DoFn<JsonObject, String>() {
			@ProcessElement
			public void processElement(ProcessContext c) {
				final JsonObject value = c.element();
				c.output(jsonb.toJson(value));
			}
		})).apply(ElasticsearchIO.write().withConnectionConfiguration(conf));
	}
	
	
	
}
