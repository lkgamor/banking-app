package com.louisga.banking.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = CentrifugalDto.CentrifugalDtoBuilder.class)
public class CentrifugalDto<T> {

    @Builder.Default
    private String method = "publish";

    private Params<T> params;

    @Data
    public static class Params<T> {

        private String channel;

        private T data;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class CentrifugalDtoBuilder<T> {}
}