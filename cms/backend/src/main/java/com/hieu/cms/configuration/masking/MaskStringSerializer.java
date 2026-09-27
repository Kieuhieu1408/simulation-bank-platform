package com.hieu.cms.configuration.masking;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;

public class MaskStringSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private String pattern;
    private int visibleTail;

    public MaskStringSerializer() {
    }

    public MaskStringSerializer(String pattern, int visibleTail) {
        this.pattern = pattern;
        this.visibleTail = visibleTail;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        if (value.isEmpty()) {
            gen.writeString(value);
            return;
        }
        if (value.length() <= visibleTail) {
            gen.writeString(pattern.repeat(value.length()));
        } else {
            String masked = pattern.repeat(value.length() - visibleTail) + value.substring(value.length() - visibleTail);
            gen.writeString(masked);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            MaskString maskString = property.getAnnotation(MaskString.class);
            if (maskString != null) {
                return new MaskStringSerializer(maskString.pattern(), maskString.visibleTail());
            }
        }
        return this;
    }
}
