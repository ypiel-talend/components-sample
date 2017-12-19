package org.talend.components.servicenow.service.http.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.talend.components.servicenow.configuration.TableRecord;
import org.talend.sdk.component.api.service.http.Encoder;

public class TableRecordToJsonEncoder implements Encoder {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] encode(final Object value) {
        if (value == null) {
            return null;
        }

        if (TableRecord.class.isInstance(value)) {
            throw new RuntimeException("Unsupported type " + value.getClass().getCanonicalName()
                    + ". Expected " + TableRecord.class.getCanonicalName());
        }

        final TableRecord record = TableRecord.class.cast(value);
        try {
            return mapper.writeValueAsBytes(record.getData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
