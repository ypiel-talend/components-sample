package org.talend.components.servicenow.service.http.codec;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.http.ContentType;
import org.talend.sdk.component.api.service.http.Encoder;

@ContentType("application/json")
public class JsonEncoder implements Encoder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] encode(final Object value) {
        if (value == null) {
            return null;
        }

        if (ObjectMap.class.isInstance(value)) {
            final ObjectMap record = ObjectMap.class.cast(value);
            try {
                return mapper.writeValueAsBytes(record.keys().stream()
                        .filter(k -> record.get(k) != null)
                        .collect(toMap(identity(), record::get)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("Unsupported type " + value.getClass().getCanonicalName()
                + ". Expected " + ObjectMap.class.getCanonicalName());
    }
}
