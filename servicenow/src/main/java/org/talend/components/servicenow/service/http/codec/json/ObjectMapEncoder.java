package org.talend.components.servicenow.service.http.codec.json;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.http.Encoder;

public class ObjectMapEncoder implements Encoder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] encode(final Object value) {
        if (value == null) {
            return null;
        }

        if (!ObjectMap.class.isInstance(value)) {
            throw new RuntimeException("Unsupported type " + value.getClass().getCanonicalName()
                    + ". Expected " + ObjectMap.class.getCanonicalName());
        }

        final ObjectMap record = ObjectMap.class.cast(value);
        try {
            return mapper.writeValueAsBytes(record.keys().stream()
                    .filter(k -> record.get(k) != null)
                    .collect(toMap(identity(), record::get)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
