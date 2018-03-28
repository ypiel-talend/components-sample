package org.talend.component.beam.elasticsearch;

import java.io.Serializable;

import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO;
import org.apache.beam.sdk.io.elasticsearch.ElasticsearchIO.ConnectionConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("elasticsearchDataset")
@GridLayout({ 
	@GridLayout.Row("dataStore"), 
	@GridLayout.Row("index"),
    @GridLayout.Row("type")
	})
public class ElasticSearchDataSet implements Serializable {
	
	@Option
	@Documentation("elasticsearch datastore")
	private ElasticSearchDataStore dataStore;
	
	@Option
	@Documentation("Index for elasticsearch")
	private String index;
	
	@Option
	@Documentation("Type for elasticsearch")
	private String type;
	
	public ElasticsearchIO.ConnectionConfiguration asConnectionConfiguration() {
		ElasticsearchIO.ConnectionConfiguration conf = ConnectionConfiguration.create(
				getDataStore().getNodeAddresses(), 
				getIndex(), getType());
		if(getDataStore().getUsername() != null && !getDataStore().getUsername().isEmpty()) {
			conf = conf.withUsername(getDataStore().getUsername())
			.withPassword(getDataStore().getPassword());
		}
		return conf;
	}
	
}