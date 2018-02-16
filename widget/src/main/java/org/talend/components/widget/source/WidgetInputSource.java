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

    private int remaining = 5;

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
        if (remaining-- > 0) {
            final WidgetInputRecord record = new WidgetInputRecord();
            record.setC1("c1");
            record.setC2(true);
            record.setC3(Math.random());
            record.setC4((int) (Math.random() * 100));
            return record;
        }
        return null;
    }

    @PreDestroy
    public void release() {
    }
}