package org.talend.component.beam.elasticsearch;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Map;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.input.PartitionMapper;

import lombok.Data;

@Icon(Icon.IconType.DB_INPUT)
@PartitionMapper(name = "Input")
public class ElasticSearchSource extends PTransform<PBegin, PCollection<JsonObject>> {
	
	private final Config config;
	
	private final JsonBuilderFactory builder;
	
	public ElasticSearchSource(final Config config, final JsonBuilderFactory builder) {
		this.config = config;
		this.builder = builder;
	}

	@Override
	public PCollection<JsonObject> expand(PBegin input) {
		return input
		          .apply(Create.of((Void) null))
		          .apply(ParDo.of(new Read(config, builder)));
	}
	
	public static class Read extends DoFn<Void, JsonObject> {
		
		private final Config config;
		
		private final JsonBuilderFactory builder;
		
		private TransportClient client;
		
		public Read(final Config config, final JsonBuilderFactory builder) {
			this.config = config;
			this.builder = builder;
		}
		
		@Setup
		public void setup() throws Exception {
			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), config.getPort()));
		}
		
		@ProcessElement
	    public void processElement(ProcessContext context) throws Exception {
			GetResponse response = client.prepareGet(config.getIndex(), config.getType(), config.getId()).get();
			Map<String, Object> fields = response.getSourceAsMap();
			JsonObjectBuilder resObjectBuilder = builder.createObjectBuilder(fields);
			context.output(resObjectBuilder.build());
		}
		
		@Teardown
		public void teardown() throws Exception {
			client.close();
		}
		
	}
	
	@Data
	    // todo: dataset/datastore? not needed for the demo
	@GridLayout({ @GridLayout.Row("hostname"), @GridLayout.Row("port"), @GridLayout.Row("index"),
	            @GridLayout.Row("type"), @GridLayout.Row("id") })
	public static class Config implements Serializable {
		
		@Option
		private String hostname;
		
		@Option
		private int port;
		
		@Option
		private String index;
		
		@Option
		private String type;
		
		@Option
		private String id;
		
	}

}
