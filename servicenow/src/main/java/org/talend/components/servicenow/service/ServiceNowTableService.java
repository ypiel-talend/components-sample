/*
 *  Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.talend.components.servicenow.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.talend.components.servicenow.configuration.BasicAuthConfig;
import org.talend.components.servicenow.messages.Messages;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import static org.talend.components.servicenow.configuration.BasicAuthConfig.NAME;
import static org.talend.components.servicenow.configuration.TableAPIConfig.Proposable_GetTableFields;

@Service
public class ServiceNowTableService {

    private volatile Map<String, Values> cachedTableFields;

    @HealthCheck(value = NAME)
    public HealthCheckStatus healthCheck(@Option(BasicAuthConfig.NAME) BasicAuthConfig dataStore, final Messages i18n) {
        try (ServiceNowRestClient client = new ServiceNowRestClientBuilder(dataStore, i18n).clientV2()) {
            client.table().healthCheck();
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, e.getLocalizedMessage());
        }
        return new HealthCheckStatus(HealthCheckStatus.Status.OK, "the data store is valid");
    }

    @DynamicValues(Proposable_GetTableFields)
    public Values getTableFields() {
        if (cachedTableFields == null) {
            synchronized (this) {
                if (cachedTableFields == null) {
                    final Collection<Values.Item> fields = Collections.emptyList();

                }
            }
        }
        return null;
    }

}
