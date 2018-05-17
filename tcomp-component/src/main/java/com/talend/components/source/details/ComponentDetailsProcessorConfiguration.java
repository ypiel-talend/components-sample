package com.talend.components.source.details;

import java.io.Serializable;

import com.talend.components.source.TCompStore;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
                    // the generated layout put one configuration entry per line,
                    // customize it as much as needed
                    @GridLayout.Row({ "dataStore" }),
            })
@Documentation("TODO fill the documentation for this configuration")
public class ComponentDetailsProcessorConfiguration implements Serializable {

    @Option
    @Documentation("Base url of TCOMP server.")
    private TCompStore dataStore;

    public TCompStore getDataStore() {
        return dataStore;
    }

    public ComponentDetailsProcessorConfiguration setDataStore(TCompStore dataStore) {
        this.dataStore = dataStore;
        return this;
    }
}