package com.hieu.moneybank.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponsePage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int number,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") Long totalElements) {
        super(
                content != null ? content : new ArrayList<>(),
                PageRequest.of(Math.max(0, number), size > 0 ? size : 20),
                totalElements != null ? totalElements : (content != null ? content.size() : 0)
        );
    }

    public RestResponsePage(List<T> content) {
        super(content != null ? content : new ArrayList<>());
    }
}
