package org.talend.components.servicenow.output;

import java.io.Serializable;

import org.talend.sdk.component.api.processor.data.ObjectMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reject implements Serializable {

    private int code;

    private String errorMessage;

    private String errorDetail;

    private ObjectMap record;

}
