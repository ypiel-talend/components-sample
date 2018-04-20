package org.talend.components.httpclient.service.codec;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.talend.sdk.component.api.service.http.ContentType;
import org.talend.sdk.component.api.service.http.Decoder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.RequiredArgsConstructor;

@ContentType("application/xml")
@RequiredArgsConstructor
public class XmlToJsonDecoder implements Decoder {

    private final JsonBuilderFactory jsonBuilderFactory;

    private SAXParserFactory saxParserFactory;

    @Override
    public JsonValue decode(final byte[] value, final Type expectedType) {
        if (value == null || value.length == 0) {
            return JsonValue.NULL;
        }
        if (JsonValue.class != expectedType) {
            throw new IllegalStateException("Unsupported type '" + expectedType + "'");
        }

        return toJson(new ByteArrayInputStream(value));
    }

    private JsonValue toJson(final InputStream stream) {
        try {
            final SAXParser parser = getSaxParserFactory().newSAXParser();
            final ObjectHandler handler = new ObjectHandler();
            handler.stack = new LinkedList<>();
            handler.stack.add(handler);
            parser.parse(stream, handler);
            return handler.builder.build();
        } catch (final IOException | ParserConfigurationException | SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    public SAXParserFactory getSaxParserFactory() {
        if (saxParserFactory == null) {
            saxParserFactory = SAXParserFactory.newInstance();
        }
        return saxParserFactory;
    }

    private class ObjectHandler extends DefaultHandler {

        private JsonObjectBuilder builder;

        private String name;

        private Attributes attributes;

        private StringBuilder characters;

        private LinkedList<ObjectHandler> stack;

        private final List<ObjectHandler> children = new LinkedList<>();

        @Override
        public void startElement(final String uri, final String localName, final String qName,
                final Attributes attributes) {
            final ObjectHandler last = stack.getLast();
            last.onStartElement(qName, attributes);
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) {
            stack.getLast().onCharacter(ch, start, length);
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) {
            stack.removeLast().onEndElement();
        }

        private void onStartElement(final String qName, final Attributes attributes) {
            if (this.name == null) {
                this.name = qName;
                this.attributes = attributes;
            } else {
                final ObjectHandler handler = new ObjectHandler();
                handler.name = qName;
                handler.attributes = attributes;
                handler.stack = stack;
                stack.add(handler);
                children.add(handler);
            }
        }

        private void onCharacter(final char[] ch, final int start, final int length) {
            if (characters == null) {
                characters = new StringBuilder();
            }
            characters.append(ch, start, length);
        }

        private void onEndElement() {
            builder = jsonBuilderFactory.createObjectBuilder();
            if (attributes != null && attributes.getLength() > 0) {
                builder.add("__attributes__", IntStream.range(0, attributes.getLength())
                        .boxed()
                        .collect(
                                jsonBuilderFactory::createObjectBuilder,
                                (builder, idx) -> builder.add(attributes.getQName(idx), attributes.getValue(idx)),
                                JsonObjectBuilder::addAll)
                        .build());
            }

            if (children.isEmpty()) {//simple value
                if (characters == null) {
                    builder.addNull(this.name);
                } else {
                    builder.add(this.name, this.characters.toString());
                    characters = null;
                }
            } else { // object//or array
                //if multiple child with same name => it's an array
                final List<String> arrayElement = children.stream().map(c -> c.name)
                        .collect(groupingBy(identity(), counting()))
                        .entrySet().stream().filter(e -> e.getValue() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                final JsonObjectBuilder object = jsonBuilderFactory.createObjectBuilder();
                if (!arrayElement.isEmpty()) {
                    final JsonArrayBuilder array = jsonBuilderFactory.createArrayBuilder();
                    children.stream()
                            .filter(c -> arrayElement.contains(c.name))
                            .forEach(c -> array.add(c.builder.build().get(c.name)));

                    object.add(arrayElement.get(0), array);
                }
                children.stream()
                        .filter(c -> !arrayElement.contains(c.name))
                        .forEach(c -> object.addAll(c.builder));
                builder.add(this.name, object);
                children.clear();
            }
        }
    }

}
