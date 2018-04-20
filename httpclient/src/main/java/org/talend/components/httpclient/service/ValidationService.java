package org.talend.components.httpclient.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;

@Service
public class ValidationService {

    public static final String URL = "ValidationService/URL";

    @AsyncValidation(URL)
    public ValidationResult validateURL(String url) {
        try {
            new URL(url);
            return new ValidationResult(ValidationResult.Status.OK, null);
        } catch (MalformedURLException e) {
            return new ValidationResult(ValidationResult.Status.KO, e.getMessage());
        }
    }
}
