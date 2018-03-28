package org.talend.component.beam.elasticsearch.source;

import java.io.Serializable;

import org.talend.component.beam.elasticsearch.ElasticSearchDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@GridLayout({ 
	@GridLayout.Row("dataset"), 
	@GridLayout.Row("query")
})
@Documentation("Configuration class for Elasticsearch Source component")
public class ElasticSearchSourceConfiguration implements Serializable {

	@Option
	@Documentation("Elasticsearch Dataset")
	private ElasticSearchDataSet dataset;
	
	@Option
	@Documentation("Search query for elasticsearch")
	private String query;
	
}
