package org.talend.components.httpclient.service.codec;

import static java.util.stream.Collectors.joining;
import static org.talend.components.httpclient.UrlEncoder.queryEncode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.talend.components.httpclient.configuration.RequestBody;
import org.talend.sdk.component.api.service.http.Encoder;

public class RequestBodyEncoder implements Encoder {

    @Override
    public byte[] encode(final Object value) {
        if (value == null) {
            return null;
        }
        if (!RequestBody.class.isInstance(value)) {
            throw new IllegalStateException("Request body need to be of type " + RequestBody.class.getName());
        }
        RequestBody body = RequestBody.class.cast(value);
        switch (body.getType()) {
        case RAW:
            return body.getRawValue().getBytes(StandardCharsets.UTF_8);
        case BINARY:
            throw new UnsupportedOperationException("not yet supported");
        case X_WWW_FORM_URLENCODED:
            return Base64.getUrlEncoder()
                    .encode(body.getParams()
                            .stream()
                            .map(param -> param.getKey() + "=" + queryEncode(param.getValue()))
                            .collect(joining("&"))
                            .getBytes(StandardCharsets.UTF_8));
        case FORM_DATA:
            return body.getParams().stream().map(param -> param.getKey() + "=" + queryEncode(param.getValue()))
                    .collect(joining("\n")).getBytes(StandardCharsets.UTF_8);
        default:
            return null;
        }
    }
}
