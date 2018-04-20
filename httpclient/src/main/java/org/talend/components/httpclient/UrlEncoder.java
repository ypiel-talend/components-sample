package org.talend.components.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncoder {

    private static final String PATH_RESERVED_CHARACTERS = "=@/:!$&\'(),;~";

    private static final String QUERY_RESERVED_CHARACTERS = "?/,";

    public static String queryEncode(final String value) {
        return componentEncode(value);
    }

    private static String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String componentEncode(final String value) {
        final StringBuilder buffer = new StringBuilder();
        final StringBuilder bufferToEncode = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            final char currentChar = value.charAt(i);
            if (QUERY_RESERVED_CHARACTERS.indexOf(currentChar) != -1) {
                if (bufferToEncode.length() > 0) {
                    buffer.append(urlEncode(bufferToEncode.toString()));
                    bufferToEncode.setLength(0);
                }
                buffer.append(currentChar);
            } else {
                bufferToEncode.append(currentChar);
            }
        }
        if (bufferToEncode.length() > 0) {
            buffer.append(urlEncode(bufferToEncode.toString()));
        }
        return buffer.toString();
    }

}
