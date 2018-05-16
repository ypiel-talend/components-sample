package com.talend.components.source;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "monitored_file" }),
    @GridLayout.Row({ "times" })
})
@Documentation("Needed configuration to refresh compo.")
public class TTacokitRefreshMapperConfiguration implements Serializable {
    @Option
    @Documentation("The file to monitor. If it is created or update date is changed refresh components.")
    private String monitored_file;

    @Option
    @Documentation("The number of time the refresh is done. After n times the component stop its monitoring.")
    private String times;

    public String getMonitored_file() {
        return monitored_file;
    }

    public TTacokitRefreshMapperConfiguration setMonitored_file(String monitored_file) {
        this.monitored_file = monitored_file;
        return this;
    }

    public String getTimes() {
        return times;
    }

    public TTacokitRefreshMapperConfiguration setTimes(String times) {
        this.times = times;
        return this;
    }
}