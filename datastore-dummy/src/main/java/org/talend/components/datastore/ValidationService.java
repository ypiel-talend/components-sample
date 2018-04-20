package org.talend.components.datastore;

import static java.util.Collections.singletonList;

import java.net.MalformedURLException;
import java.net.URL;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;
import org.talend.sdk.component.api.service.schema.Schema;
import org.talend.sdk.component.api.service.schema.Type;

@Service
public class ValidationService {

    public static final String URL = "ValidationService/URL";

    public static final String HEALTH_CHECK = "ValidationService/HEALTH_CHECK";

    @HealthCheck(value = HEALTH_CHECK)
    public HealthCheckStatus healthCheck(@Option CDataStore config) {
        if (config.getEmail() == null || config.getEmail().isEmpty()) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "email can't be empty");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Ok");
    }

    @AsyncValidation(URL)
    public ValidationResult validateURL(String url) {
        try {
            new URL(url);
            return new ValidationResult(ValidationResult.Status.OK, null);
        } catch (MalformedURLException e) {
            return new ValidationResult(ValidationResult.Status.KO, e.getMessage());
        }
    }

    @DiscoverSchema("discoverSchema")
    public Schema guessTableSchema(@Option CDataSet configuration) {
        return new Schema(singletonList(new Schema.Entry("name", Type.STRING)));
    }
}
