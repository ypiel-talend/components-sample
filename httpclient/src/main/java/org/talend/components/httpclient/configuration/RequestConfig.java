package org.talend.components.httpclient.configuration;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.talend.components.httpclient.UrlEncoder.queryEncode;
import static org.talend.components.httpclient.configuration.RequestBody.Type.X_WWW_FORM_URLENCODED;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.talend.components.httpclient.configuration.auth.Authentication;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Min;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.http.Configurer;

import lombok.Data;

@Data
@DataSet
@GridLayout({
        @GridLayout.Row({ "authentication" }),
        @GridLayout.Row({ "hasQueryParam" }),
        @GridLayout.Row({ "queryParams" }),
        @GridLayout.Row({ "hasHeaders" }),
        @GridLayout.Row({ "headers" }),
        @GridLayout.Row({ "body" }),
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "authentication" }),
        @GridLayout.Row({ "connectionTimeout" }),
        @GridLayout.Row({ "readTimeout" }),
        @GridLayout.Row({ "extractor" })
})
@Documentation("")
public class RequestConfig implements Serializable {

    @Option
    @Documentation("Http request authentication")
    private Authentication authentication;

    @Option
    @Documentation("Http request contains query params")
    private Boolean hasQueryParam = false;

    @Option
    @ActiveIf(target = "hasQueryParam", value = "true")
    @Documentation("Http request query params")
    private List<Param> queryParams = new ArrayList<>();

    @Option
    @ActiveIf(target = "authentication.methodType", value = { "POST", "PUT", "PATCH", "DELETE", "OPTIONS" })
    @Documentation("")
    private RequestBody body;

    @Option
    @Documentation("Http request contains headers")
    private Boolean hasHeaders = false;

    @Option
    @ActiveIf(target = "hasHeaders", value = "true")
    @Documentation("Http request headers")
    private List<Param> headers = new ArrayList<>();

    @Min(0)
    @Option
    @Documentation("Http request connection timeout")
    private Integer connectionTimeout;

    @Min(0)
    @Option
    @Documentation("Http request read timeout")
    private Integer readTimeout;

    @Option
    @Documentation("Result extractor. This is a json pointer. See the documentation for more details https://tools.ietf.org/html/rfc6901")
    private String extractor = "";

    public Map<String, String> queryParams() {
        return queryParams.stream().collect(toMap(Param::getKey, Param::getValue));
    }

    public Map<String, String> headers() {
        final Map<String, String> h = headers.stream().collect(toMap(Param::getKey, Param::getValue));
        if (body != null && hasPayLoad() && X_WWW_FORM_URLENCODED.equals(body.getType())) {
            h.put("Content-Type", "application/x-www-form-urlencoded");
        }
        return h;
    }

    public boolean hasPayLoad() {
        switch (body.getType()) {
        case RAW:
            return body.getRawValue() != null && !body.getRawValue().isEmpty();
        case BINARY:
            return body.getBinaryPath() != null && !body.getBinaryPath().isEmpty();
        case X_WWW_FORM_URLENCODED:
        case FORM_DATA:
            return body.getParams() != null && !body.getParams().isEmpty();
        default:
            return false;
        }
    }
}
