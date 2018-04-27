package org.talend.components.geo;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
public class InclusionConfiguration implements Serializable {

    @Option
    @Documentation("The longitude of the center of the circle")
    private double centerLongitude;

    @Option
    @Documentation("The latitude of the center of the circle")
    private double centerLatitude;

    @Option
    @Documentation("The bottom right point of the zone to consider")
    private double acceptedRadius;

    @Option
    @DefaultValue("longitude")
    @Documentation("The longitude column name")
    private String longitudeColumnName;

    @Option
    @DefaultValue("latitude")
    @Documentation("The latitude column name")
    private String latitudeColumnName;
}
