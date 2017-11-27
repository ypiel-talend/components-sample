package org.talend.components.widget.source;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;

import org.talend.components.widget.service.WidgetService;

public class WidgetInputSource implements Serializable {

    private final WidgetInputMapperConfiguration configuration;

    private final WidgetService service;

    public WidgetInputSource(@Option("configuration") final WidgetInputMapperConfiguration configuration,
            final WidgetService service) {
        this.configuration = configuration;
        this.service = service;
    }

    @PostConstruct
    public void init() {
    }

    @Producer
    public WidgetInputRecord next() {
        return null;
    }

    @PreDestroy
    public void release() {
    }
}