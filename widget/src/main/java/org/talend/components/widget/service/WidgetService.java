package org.talend.components.widget.service;

import static java.util.stream.Collectors.toList;

import java.util.stream.Stream;

import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.DynamicValues;
import org.talend.sdk.component.api.service.completion.Values;

@Service
public class WidgetService {

    @DynamicValues("sample")
    public Values sampleValues() {
        return new Values(Stream.of(new Values.Item("a", "A"), new Values.Item("b", "B"), new Values.Item("c", "The C value"))
                .collect(toList()));
    }

}