package org.talend.components.servicenow.service.http.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.json.bind.Jsonb;
import javax.json.bind.spi.JsonbProvider;

import org.talend.sdk.component.api.service.http.ContentType;
import org.talend.sdk.component.api.service.http.Decoder;
import org.talend.sdk.component.api.service.http.Encoder;

@ContentType("application/json")
public class JsonCodec implements Encoder, Decoder {

    private final Jsonb jsonb = JsonbProvider.provider().create().build();

    @Override
    public Object decode(final byte[] value, final Type expectedType) {
        if (!Class.class.isInstance(expectedType)) {
            throw new IllegalArgumentException("Unsupported type: " + expectedType);
        }
        final Class<?> clazz = Class.class.cast(expectedType);
        return jsonb.fromJson(new ByteArrayInputStream(value), clazz);
    }

    @Override
    public byte[] encode(final Object value) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (final OutputStreamWriter out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            jsonb.toJson(value, out);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return outputStream.toByteArray();
    }

}
