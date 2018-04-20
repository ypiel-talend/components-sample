package org.talend.components.datastore;

import static org.talend.components.datastore.ValidationService.HEALTH_CHECK;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Checkable;
import org.talend.sdk.component.api.configuration.action.Validable;
import org.talend.sdk.component.api.configuration.constraint.Max;
import org.talend.sdk.component.api.configuration.constraint.Min;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore
@Checkable(HEALTH_CHECK)
public class CDataStore {

    @Option
    @Validable(ValidationService.URL)
    @Documentation("")
    private String url;

    @Option
    @Pattern("/^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$/")
    @Documentation("")
    private String email;

    @Option
    @Credential
    @Documentation("")
    private String password;

    @Option
    @Pattern("/^\\d+$/")
    @DefaultValue("00001112321")
    @Documentation("")
    private String phone;

    @Option
    @Min(3)
    @Max(10)
    @Documentation("")
    private int minMax = 4;

}
