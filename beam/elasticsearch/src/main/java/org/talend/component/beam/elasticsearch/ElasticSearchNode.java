package org.talend.component.beam.elasticsearch;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;

import lombok.Data;

@Data
public class ElasticSearchNode implements Serializable {

	@Option
	private String address;
	
	@Option
	private String port;
	
}
