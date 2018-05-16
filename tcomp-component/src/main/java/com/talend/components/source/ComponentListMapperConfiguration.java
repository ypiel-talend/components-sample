package com.talend.components.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "dataStore" }),
    @GridLayout.Row({ "filter_family" }),
    @GridLayout.Row({ "filter_name" })
})
@Documentation("Needed configuration to retrieve components filtered by family and name.")
public class ComponentListMapperConfiguration implements Serializable {

    @Option
    @Documentation("Base url of TCOMP server.")
    private TCompStore dataStore;

    @Option
    @Documentation("The filter on components family")
    private String filter_family;

    @Option
    @Documentation("The filter on components name")
    private String filter_name;

    public String getFilter_family() {
        return filter_family;
    }

    public ComponentListMapperConfiguration setFilter_family(String filter_family) {
        this.filter_family = filter_family;
        return this;
    }

    public String getFilter_name() {
        return filter_name;
    }

    public ComponentListMapperConfiguration setFilter_name(String filter_name) {
        this.filter_name = filter_name;
        return this;
    }

    public TCompStore getDataStore() {
        return dataStore;
    }

    public void setDataStore(TCompStore dataStore) {
        this.dataStore = dataStore;
    }
}