package org.talend.components.servicenow.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

import org.talend.sdk.component.api.processor.data.ObjectMap;

@Data
@AllArgsConstructor
public class TableRecord implements Serializable {

    @ObjectMap.Any
    private Map<String, Object> data;

}