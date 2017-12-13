package org.talend.components.servicenow.configuration;

import java.io.Serializable;
import java.util.Map;

import org.talend.sdk.component.api.processor.data.ObjectMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableRecord implements Serializable {

    @ObjectMap.Any
    private Map<String, Object> data;

}