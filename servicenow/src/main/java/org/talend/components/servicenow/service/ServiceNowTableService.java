/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.talend.components.servicenow.service;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.talend.components.servicenow.configuration.BasicAuthConfig.NAME;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.configuration.CommonConfig;
import org.talend.components.servicenow.configuration.TableDataSet;
import org.talend.components.servicenow.messages.Messages;
import org.talend.components.servicenow.service.http.TableApiClient;
import org.talend.components.servicenow.source.ServiceNowTableSource;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.processor.data.ObjectMap;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.asyncvalidation.AsyncValidation;
import org.talend.sdk.component.api.service.asyncvalidation.ValidationResult;
import org.talend.sdk.component.api.service.cache.Cached;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;
import org.talend.sdk.component.api.service.schema.DiscoverSchema;
import org.talend.sdk.component.api.service.schema.Schema;
import org.talend.sdk.component.api.service.schema.Type;

@Service
public class ServiceNowTableService {

    private volatile Map<String, Values> cachedTableFields;

    @HealthCheck(value = NAME)
    public HealthCheckStatus healthCheck(@Option(BasicAuthConfig.NAME) BasicAuthConfig dt, TableApiClient client) {
        try {
            client.healthCheck(dt.getAuthorizationHeader());
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getLocalizedMessage());
        }

        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "the data store is valid");
    }

    @AsyncValidation("urlValidation")
    public ValidationResult validateUrl(final String url) {
        try {
            new URL(url);
            return new ValidationResult(ValidationResult.Status.OK, null);
        } catch (MalformedURLException e) {
            return new ValidationResult(ValidationResult.Status.KO, e.getMessage());
        }
    }

    @Cached
    @DynamicValues(CommonConfig.Proposable_GetTableFields)
    public Values getTableFields() {
        // todo when dynamic values can have params
        return new Values(asList(new Values.Item("value-1", "Value 1"), new Values.Item("value-2", "Value 2")));
    }

    @DiscoverSchema("guessTableSchema")
    public Schema guessTableSchema(final TableDataSet dataSet, final TableApiClient client, final Messages i18n) {
        dataSet.setMaxRecords(1); // limit result to 1 record to infer the schema
        final ServiceNowTableSource source = new ServiceNowTableSource(dataSet, i18n, client);
        source.init();
        final ObjectMap record = source.next();
        return new Schema(record.keys()
                .stream()
                .map(k -> new Schema.Entry(k, Type.STRING))
                .collect(toList()));
    }

}
