package org.talend.components.servicenow.configuration;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import org.talend.sdk.component.api.processor.data.ObjectMap;

@Data
public class ServiceNowRecord {

    @ObjectMap.Any
    private Map<String, Object> data = new HashMap<>();

}