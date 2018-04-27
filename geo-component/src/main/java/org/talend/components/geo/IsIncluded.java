package org.talend.components.geo;

import java.io.Serializable;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Version
@Processor(name = "IsIncluded")
@Icon(custom = "world", value = Icon.IconType.CUSTOM)
public class IsIncluded implements Serializable {

    private final InclusionConfiguration configuration;

    @ElementListener
    public void onElement(@Input final JsonObject element, @Output final OutputEmitter<JsonObject> output) {
        if (isIncluded(element)) {
            output.emit(element);
        }
    }

    private boolean isIncluded(final JsonObject element) {
        final double latitude = toNumber(element, element.get(configuration.getLatitudeColumnName()));
        final double longitude = toNumber(element, element.get(configuration.getLongitudeColumnName()));
        final double distance = Math.sqrt(Math.pow(latitude - configuration.getCenterLatitude(), 2)
                + Math.pow(longitude - configuration.getCenterLongitude(), 2));
        return distance <= configuration.getAcceptedRadius();
    }

    private double toNumber(final JsonValue root, final JsonValue jsonValue) {
        if (jsonValue == null) {
            throw new IllegalArgumentException("No value in " + root);
        }
        switch (jsonValue.getValueType()) {
        case STRING:
            return Double.parseDouble(JsonString.class.cast(jsonValue).getString());
        case NUMBER:
            return JsonNumber.class.cast(jsonValue).doubleValue();
        default:
            throw new IllegalArgumentException("Can't read " + root + " > " + jsonValue);
        }
    }
}
