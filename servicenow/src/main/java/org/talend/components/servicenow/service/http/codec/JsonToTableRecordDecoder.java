package org.talend.components.servicenow.service.http.codec;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.sdk.component.api.service.http.Decoder;

public class JsonToTableRecordDecoder implements Decoder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object decode(final byte[] value, final Type expectedType) {
        if (value == null) {
            return null;
        }

        if (TableApiClient.Status.class.equals(expectedType)) {
            try {
                return mapper.readValue(value, TableApiClient.Status.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Map<String, Object> result = mapper.readValue(value, HashMap.class);
            if (result != null && result.containsKey("result")) {
                return ((List<Map<String, Object>>) result.get("result")).stream()
                        .map(TableRecord::new)
                        .collect(toList());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
