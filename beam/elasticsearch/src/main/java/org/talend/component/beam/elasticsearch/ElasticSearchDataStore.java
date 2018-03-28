package org.talend.component.beam.elasticsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("ElasticSearchDatastore")
@GridLayout({ 
	@GridLayout.Row("username"), 
	@GridLayout.Row("password"), 
    @GridLayout.Row("nodes") })
public class ElasticSearchDataStore implements Serializable {

	@Option
	@Documentation("Table representing elasticsearch nodes")
	private List<ElasticSearchNode> nodes;
	
	@Option
	@Documentation("Username to access elasticsearch node(if required)")
	private String username;
	
	@Option
	@Credential
	@Documentation("Password to access elasticsearch node(if required)")
	private String password;
	
	public String[] getNodeAddresses() {
		List<String> nodeAddresses = new ArrayList<>();
		for(ElasticSearchNode node : nodes) {
			nodeAddresses.add("http://" + node.getAddress() + ":" + node.getPort());
		}
		return nodeAddresses.toArray(new String[0]);
	}
	
}
