package com.lodovicoazzini.reserve.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ControllerUtils {

    private ControllerUtils() {
    }

    public static ResponseEntity<String> encodeResponse(final Object content, final HttpStatus successStatus) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final String encoded = mapper.writeValueAsString(content);
            return new ResponseEntity<>(encoded, successStatus);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
