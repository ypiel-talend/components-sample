package org.talend.components.servicenow.service.http.codec;

import java.lang.reflect.Type;

import org.talend.sdk.component.api.service.http.ContentType;
import org.talend.sdk.component.api.service.http.Decoder;

/**
 * We don't expect the api to send any non application/json content,
 * this only for error handling when the instance is not up & running
 */
@ContentType
public class InvalidContentDecoder implements Decoder {

    @Override
    public Object decode(final byte[] value, final Type expectedType) {
        throw new RuntimeException(
                "Your ServiceSow instance is down or hibernating. Please check that your instance is up and running !");
    }
}
