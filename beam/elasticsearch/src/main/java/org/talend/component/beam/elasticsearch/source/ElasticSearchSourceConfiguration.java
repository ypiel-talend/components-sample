package org.talend.component.beam.elasticsearch.source;

import java.io.Serializable;

import org.talend.component.beam.elasticsearch.ElasticSearchDataSet;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import lombok.Data;

@Data
@GridLayout({ 
	@GridLayout.Row("dataset"), 
	@GridLayout.Row("query")
})
public class ElasticSearchSourceConfiguration implements Serializable {

	@Option
	private ElasticSearchDataSet dataset;
	
	@Option
	private String query;
	
}
