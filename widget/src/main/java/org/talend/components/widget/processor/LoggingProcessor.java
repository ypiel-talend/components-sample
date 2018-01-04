package org.talend.components.widget.processor;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;

import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.processor.data.ObjectMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Icon(Icon.IconType.LOGO)
@Processor(name = "logging")
public class LoggingProcessor implements Serializable {
    @BeforeGroup
    public void onBefore() {
        log.info("Starting");
    }

    @ElementListener
    public void onElement(final ObjectMap map) {
        log.info("->\n" + map.keys().stream().map(k -> k + "=" + String.valueOf(map.get(k))).collect(joining("\n")));
    }
}
