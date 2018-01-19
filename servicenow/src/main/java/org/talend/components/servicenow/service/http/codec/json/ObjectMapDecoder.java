package org.talend.components.servicenow.service.http.codec.json;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.processor.data.FlatObjectMap;
import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.http.Decoder;

public class ObjectMapDecoder implements Decoder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object decode(final byte[] value, final Type expectedType) {
        if (value == null || value.length == 0) {
            return null;
        }

        if (TableApiClient.Status.class.equals(expectedType)) {  //decode error
            try {
                return mapper.readValue(value, TableApiClient.Status.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (isObjectMapList(expectedType)) {// list of ObjectMap records
            try {
                Map<String, Object> result = mapper.readValue(value, HashMap.class);
                if (result != null && result.containsKey("result")) {
                    return ((List<Map<String, Object>>) result.get("result")).stream()
                            .map(FlatObjectMap::new)
                            .collect(toList());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!ObjectMap.class.isAssignableFrom((Class<?>) expectedType)) {
            throw new RuntimeException("Unsupported type " + expectedType.getTypeName()
                    + ". Expected " + ObjectMap.class.getCanonicalName());
        }

        try {
            Map<String, Object> result = mapper.readValue(value, HashMap.class);
            if (result != null && result.containsKey("result")) {
                return new FlatObjectMap((Map<String, Object>) result.get("result"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    private boolean isObjectMapList(final Type expectedType) {
        if (ParameterizedType.class.isAssignableFrom(expectedType.getClass())) {
            ParameterizedType type = (ParameterizedType) expectedType;
            return List.class.isAssignableFrom((Class<?>) type.getRawType()) && ObjectMap.class.isAssignableFrom(
                    (Class<?>) type.getActualTypeArguments()[0]);
        }

        return false;
    }
}
