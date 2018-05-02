package org.talend.components.demo.service;

import static java.util.Arrays.asList;

import org.talend.components.demo.source.AuthConfiguration;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

@Service
public class Demo_componentService {

    @HealthCheck
    public HealthCheckStatus healthCheck(@Option AuthConfiguration dataStore) {
        if (dataStore.getUrl() == null || dataStore.getUrl().contains("talend")) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, "Error !");
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "Success !");
    }

    @AsyncValidation("validateURL")
    public ValidationResult validateURl(String url) {

        if (url == null || url.contains("talend")) {
            return new ValidationResult(ValidationResult.Status.KO, "The URL is not valid");
        }

        return new ValidationResult(ValidationResult.Status.OK, "The URL is valid");
    }

    @DynamicValues("vendors")
    public Values vendors() {
        return new Values(asList(new Values.Item("1", "Creative Mobile Technologies, LLC"),
                new Values.Item("2", "VeriFone Inc")));
    }

}