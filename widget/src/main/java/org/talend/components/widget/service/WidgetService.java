package org.talend.components.widget.service;

import static java.util.stream.Collectors.toList;
import static org.talend.sdk.component.api.service.asyncvalidation.ValidationResult.Status.KO;

import java.util.stream.Stream;

import org.talend.components.widget.source.ConnectionConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult.Status;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@Service
public class WidgetService {

    @DynamicValues("sample")
    public Values sampleValues() {
        return new Values(Stream.of(new Values.Item("a", "A"), new Values.Item("b", "B"), new Values.Item("c", "The C value"))
                .collect(toList()));
    }

    @AsyncValidation("tableValidation")
    public ValidationResult validateTable(@Option("a1") final String tableName) {
        if ("toto".equals(tableName)) {
            return new ValidationResult(Status.OK, "It's ok");
        }
        return new ValidationResult(Status.KO, "Validation failed");
    }
    
    @AsyncValidation("pwdConfirm")
    public ValidationResult confirmPassword(@Option("p1") final String password, @Option("p2") final String passwordConfirmation) {
    	if (password == null || passwordConfirmation == null) {
    		return new ValidationResult(KO, "Passwords should not be null");
    	}
        if (!password.equals(passwordConfirmation)) {
            return new ValidationResult(KO, "Passwords should be equal");
        }
        return new ValidationResult(Status.OK, "OK");
    }
    
    @HealthCheck("testConnection")
    public HealthCheckStatus healthCheck(@Option("conn") final ConnectionConfiguration conn) {
        final String user = conn.getUser();
        if (user == null || !user.equals("Ivan")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "User should be Ivan");
        }
        final String url = conn.getURL();
        if (url == null || !url.equals("https://Talend.com")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "URL should be https://Talend.com");
        }
        final String password = conn.getPassword();
        if (password == null || !password.equals("123456")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Password should be 123456");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Connection ok");
    }
    
}