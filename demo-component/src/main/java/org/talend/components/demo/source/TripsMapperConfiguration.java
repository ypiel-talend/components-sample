package org.talend.components.demo.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Proposable;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
        // the generated layout put one configuration entry per line,
        // customize it as much as needed
        @GridLayout.Row({ "auth" }),
        @GridLayout.Row({ "vendor" }),
        @GridLayout.Row({ "distanceMin", "distanceMax" }),
})
@Documentation("TODO fill the documentation for this configuration")
public class TripsMapperConfiguration implements Serializable {

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private AuthConfiguration auth;

    @Proposable("vendors")
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private int vendor = 1;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private double distanceMin = 0;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private double distanceMax = 4;

    public AuthConfiguration getAuth() {
        return auth;
    }

    public TripsMapperConfiguration setAuth(AuthConfiguration auth) {
        this.auth = auth;
        return this;
    }

    public int getVendor() {
        return vendor;
    }

    public TripsMapperConfiguration setVendor(int vendor) {
        this.vendor = vendor;
        return this;
    }

    public double getDistanceMin() {
        return distanceMin;
    }

    public TripsMapperConfiguration setDistanceMin(double distanceMin) {
        this.distanceMin = distanceMin;
        return this;
    }

    public double getDistanceMax() {
        return distanceMax;
    }

    public TripsMapperConfiguration setDistanceMax(double distanceMax) {
        this.distanceMax = distanceMax;
        return this;
    }
}