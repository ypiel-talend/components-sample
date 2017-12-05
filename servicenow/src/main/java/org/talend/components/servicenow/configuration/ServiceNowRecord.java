package org.talend.components.servicenow.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

import org.talend.sdk.component.api.processor.data.ObjectMap;

@Data
@AllArgsConstructor
public class ServiceNowRecord {

    @ObjectMap.Any
    private Map<String, Object> data;

}