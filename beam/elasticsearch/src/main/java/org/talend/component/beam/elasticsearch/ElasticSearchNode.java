package org.talend.component.beam.elasticsearch;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
public class ElasticSearchNode implements Serializable {

	@Option
	@Documentation("Elasticsearch node address")
	private String address;
	
	@Option
	@Documentation("Elasticsearch node port")
	private String port;
	
}
