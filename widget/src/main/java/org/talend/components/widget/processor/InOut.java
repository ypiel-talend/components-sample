package org.talend.components.widget.processor;

import java.io.Serializable;

import javax.json.JsonObject;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;

@Icon(Icon.IconType.ACTIVITY)
@Processor(name = "passthrough")
public class InOut implements Serializable {
    @ElementListener
    public JsonObject onElement(final JsonObject map) {
        return map;
    }
}
