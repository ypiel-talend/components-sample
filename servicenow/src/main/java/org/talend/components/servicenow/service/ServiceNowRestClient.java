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

import java.io.Closeable;
import java.util.List;

import org.talend.components.servicenow.configuration.ServiceNowTableDataSet;
import org.talend.components.servicenow.configuration.ServiceNowRecord;

public interface ServiceNowRestClient extends Closeable {

    String API_BASE = "/api/now";
    //API PARAMS
    String sysparm_suppress_pagination_header = "sysparm_suppress_pagination_header";

    String sysparm_offset = "sysparm_offset";

    String sysparm_limit = "sysparm_limit";

    String sysparm_exclude_reference_link = "sysparm_exclude_reference_link";

    String sysparm_query = "sysparm_query";

    String sysparm_fields = "sysparm_fields";

    String HEADER_X_Total_Count = "X-Total-Count";

    TableRestClient table();

    /**
     * client to query service now tables
     */
    interface TableRestClient {

        /**
         * @param dataSet definition of the datat set table name, size, fields...
         * @return record from the table according to the data set definition
         */
        List<ServiceNowRecord> get(final ServiceNowTableDataSet dataSet);

        /**
         * @param dataSet
         * @return number of record for the given dataSet
         */
        int count(final ServiceNowTableDataSet dataSet);

        /**
         * Calculate one record size in bytes, this is an estimation and not an exact record size
         *
         * @param tableName
         * @return an estimation of the record size in bytes
         */
        long estimateRecordBytesSize(final String tableName);

        /**
         * Calculate the hole dataset size in bytes, this is an estimation and not an exact record size
         *
         * @param dataSet
         * @return an estimation of the hole data set size in bytes
         */
        long estimateDataSetBytesSize(final ServiceNowTableDataSet dataSet);

        /**
         * Check the health of the service now api
         */
        void healthCheck() throws Exception;

    }
}
