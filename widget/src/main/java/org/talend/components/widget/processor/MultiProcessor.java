package org.talend.components.widget.processor;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Processor(name = "MultiDemo")
public class MultiProcessor implements Serializable {
    private JsonBuilderFactory factory;

    @ElementListener
    public void onElements(@Input final JsonObject v1, @Input("source2") final JsonObject v2,
                           @Output final OutputEmitter<JsonObject> mainOutput,
                           @Output("secondOutput") final OutputEmitter<JsonObject> secondOutput,
                           @Output("thirdOutput") final OutputEmitter<JsonObject> thirdOutput) {
        if (v2 != null) {
            mainOutput.emit(factory.createObjectBuilder()
                                   .add("v2", v2)
                                   .add("date", LocalDateTime.now()
                                                             .toString())
                                   .build());
        }
        if (v1 != null) {
            secondOutput.emit(factory.createObjectBuilder()
                                     .add("v1", v1)
                                     .add("datetime", LocalDateTime.now()
                                                                   .toString())
                                     .build());
        }
        thirdOutput.emit(factory.createObjectBuilder().add("got", "something").build());
    }
}
