package org.talend.components.servicenow.messages;

import org.talend.sdk.component.api.internationalization.Internationalized;

@Internationalized
public interface Messages {

    String httpError(int code, String reason, String details);
}
