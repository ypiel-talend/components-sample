// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.talend.components.service;

import org.talend.sdk.component.api.service.http.HttpClient;
import org.talend.sdk.component.api.service.http.Query;
import org.talend.sdk.component.api.service.http.Request;
import org.talend.sdk.component.api.service.http.Response;

import javax.json.JsonObject;

public interface TTacokitRestClient extends HttpClient {

    @Request(path = "api/v1/component/index", method = "GET")
    Response<JsonObject> listComponents();

    @Request(path = "api/v1/component/details", method = "GET")
    Response<JsonObject> getComponentDetails(@Query("identifiers") String identifier);

}
