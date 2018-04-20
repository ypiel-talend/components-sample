package org.talend.components.datastore;

import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet
public class CDataSet {

    @Option
    @Documentation("")
    public final CDataStore dataStoreConfig;

    @Option
    @Structure(type = Structure.Type.OUT, discoverSchema = "discoverSchema")
    @Documentation("")
    private final List<String> schema;

}
